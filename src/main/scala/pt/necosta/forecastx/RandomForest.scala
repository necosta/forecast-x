package pt.necosta.forecastx

import org.apache.spark.ml.{Model, Pipeline, PipelineModel, PipelineStage}
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.functions._
import pt.necosta.forecastx.record.RandomForestRecord

object RandomForest extends WithSpark {

  def start(dataRaw: Dataset[RandomForestRecord]): PipelineModel = {

    // Eliminates noisy logging
    spark.sparkContext.setLogLevel("ERROR")

    // Cache dataset in memory
    dataRaw.cache()

    val dfs = dataRaw
      .randomSplit(Array(0.7, 0.3), 123456L)

    val trainDf = dfs(0).withColumnRenamed("isWinner", "label")
    val crossDf = dfs(1)

    // create pipeline stages for handling categorical
    val surfaceStage = handleCategorical("surface")
    val drawSizeStage = handleCategorical("drawSize")
    val levelStage = handleCategorical("tourneyLevel")
    val entryStage = handleCategorical("entry")
    val handStage = handleCategorical("hand")
    val countryStage = handleCategorical("country")
    val roundStage = handleCategorical("round")

    val catStages = surfaceStage ++ drawSizeStage ++ levelStage ++ entryStage ++ handStage ++ countryStage ++ roundStage

    //columns for training
    val cols = Array(
      "surface_onehot",
      "drawSize_onehot",
      "tourneyLevel_onehot",
      "entry_onehot",
      "hand_onehot",
      "country_onehot",
      "round_onehot",
      "seed",
      "height",
      "age",
      "rank",
      "rankPoints"
    )

    val vectorAssembler =
      new VectorAssembler()
        .setInputCols(cols)
        .setOutputCol("features")

    val randomForestClassifier = new RandomForestClassifier()

    val pipeline = new Pipeline()
      .setStages(
        catStages ++ Array(vectorAssembler) ++ Array(randomForestClassifier))

    val model = pipeline.fit(trainDf)

    val trainScore =
      accuracyScore(model.transform(trainDf), "label", "prediction")
    val testScore =
      accuracyScore(model.transform(crossDf), "isWinner", "prediction")

    println(s"train accuracy with pipeline: $trainScore")
    println(s"test accuracy with pipeline: $testScore")

    //scoreDf(model,trainDf)

    //cross validation
    val paramMap = new ParamGridBuilder()
      .addGrid(randomForestClassifier.impurity, Array("gini", "entropy"))
      .addGrid(randomForestClassifier.maxDepth, Array(1, 2, 5))
      .addGrid(randomForestClassifier.minInstancesPerNode, Array(1, 2, 4))
      .build()

    val cvModel = crossValidation(pipeline, paramMap, trainDf)
    val trainCvScore =
      accuracyScore(cvModel.transform(trainDf), "label", "prediction")
    val testCvScore =
      accuracyScore(cvModel.transform(crossDf), "isWinner", "prediction")

    println(s"train accuracy with cross validation: $trainCvScore")
    println(s"test accuracy with cross validation: $testCvScore")

    model
  }

  def save(model: PipelineModel, modelFile: String): Unit = {
    model.save(modelFile)
  }

  def load(modelFile: String): PipelineModel = {
    PipelineModel.load(modelFile)
  }

  def score(model: PipelineModel, records: Array[RandomForestRecord]): Unit = {
    import spark.implicits._

    val dummyDs = spark.createDataset[RandomForestRecord](records)
    scoreDf(model, dummyDs.toDF())
  }

  private def crossValidation(pipeline: Pipeline,
                              paramMap: Array[ParamMap],
                              df: DataFrame): Model[_] = {
    new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(new BinaryClassificationEvaluator)
      .setEstimatorParamMaps(paramMap)
      .setNumFolds(5)
      .fit(df)
  }

  private def handleCategorical(column: String): Array[PipelineStage] = {
    val stringIndexer = new StringIndexer()
      .setInputCol(column)
      .setOutputCol(s"${column}_index")
      .setHandleInvalid("skip")
    val oneHot = new OneHotEncoder()
      .setInputCol(s"${column}_index")
      .setOutputCol(s"${column}_onehot")
    Array(stringIndexer, oneHot)
  }

  private def accuracyScore(df: DataFrame,
                            label: String,
                            predictCol: String): Double = {
    val rdd = df
      .select(predictCol, label)
      .rdd
      .map(row => (row.getDouble(0), row.getDouble(1)))
    new MulticlassMetrics(rdd).accuracy
  }

  private def scoreDf(model: PipelineModel, df: DataFrame): Unit = {
    import spark.implicits._

    val first = udf((v: org.apache.spark.ml.linalg.Vector, pred: Double) => {
      val p = (v.toArray.head * 100).floor / 100
      if (pred > 0.0) 1 - p else p
    })

    model
      .transform(df)
      .withColumn("prob", first($"probability", $"prediction"))
      .select("seed", "prob", "prediction")
      .show(20)
  }
}
