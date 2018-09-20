package pt.necosta.forecastx

import org.apache.spark.ml.{Model, Pipeline, PipelineStage}
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.sql.{DataFrame, Dataset}
import pt.necosta.forecastx.record.RandomForestRecord

object RandomForest extends WithSpark {

  def start(dataRaw: Dataset[RandomForestRecord]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")

    // ToDo: review missing values handling
    val dfs = dataRaw.na.fill(999).randomSplit(Array(0.7, 0.3), 123456L)
    val trainDf = dfs(0).withColumnRenamed("isWinner", "label")
    val crossDf = dfs(1)

    // create pipeline stages for handling categorical
    val surfaceStage = handleCategorical("surface")
    val levelStage = handleCategorical("tourneyLevel")
    val entryStage = handleCategorical("entry")
    val handStage = handleCategorical("hand")
    val iocStage = handleCategorical("ioc")

    //columns for training
    val cols = Array("surface_onehot",
                     "tourneyLevel_onehot",
                     "entry_onehot",
                     "hand_onehot",
                     "ioc_onehot",
                     "drawSize",
                     "seed",
                     "age",
                     "rank",
                     "rankPoints")
    val vectorAssembler =
      new VectorAssembler()
        .setInputCols(cols)
        .setOutputCol("features")

    val randomForestClassifier = new RandomForestClassifier()
    val preProcessStages = surfaceStage ++ levelStage ++ entryStage ++ handStage ++ iocStage ++ Array(
      vectorAssembler)
    val pipeline = new Pipeline()
      .setStages(preProcessStages ++ Array(randomForestClassifier))

    val model = pipeline.fit(trainDf)
    println(
      "train accuracy with pipeline: " + accuracyScore(model.transform(trainDf),
                                                       "label",
                                                       "prediction"))
    println(
      "test accuracy with pipeline: " + accuracyScore(model.transform(crossDf),
                                                      "isWinner",
                                                      "prediction"))

    //model
    //  .transform(trainDf)
    //  .select("label", "features", "rawPrediction", "probability", "prediction")
    //  .show(20)

    //cross validation
    val paramMap = new ParamGridBuilder()
      .addGrid(randomForestClassifier.impurity, Array("gini", "entropy"))
      .addGrid(randomForestClassifier.maxDepth, Array(1, 2, 5))
      .addGrid(randomForestClassifier.minInstancesPerNode, Array(1, 2, 4))
      .build()

    val cvModel = crossValidation(pipeline, paramMap, trainDf)
    println(
      "train accuracy with cross validation: " + accuracyScore(
        cvModel.transform(trainDf),
        "label",
        "prediction"))
    println(
      "test accuracy with cross validation: " + accuracyScore(
        cvModel.transform(crossDf),
        "isWinner",
        "prediction"))
  }

  def crossValidation(pipeline: Pipeline,
                      paramMap: Array[ParamMap],
                      df: DataFrame): Model[_] = {
    val cv = new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(new BinaryClassificationEvaluator)
      .setEstimatorParamMaps(paramMap)
      .setNumFolds(5)
    cv.fit(df)
  }

  def handleCategorical(column: String): Array[PipelineStage] = {
    val stringIndexer = new StringIndexer()
      .setInputCol(column)
      .setOutputCol(s"${column}_index")
      .setHandleInvalid("skip")
    val oneHot = new OneHotEncoder()
      .setInputCol(s"${column}_index")
      .setOutputCol(s"${column}_onehot")
    Array(stringIndexer, oneHot)
  }

  def accuracyScore(df: DataFrame,
                    label: String,
                    predictCol: String): Double = {
    val rdd = df
      .select(predictCol, label)
      .rdd
      .map(row ⇒ (row.getDouble(0), row.getDouble(1)))
    new MulticlassMetrics(rdd).accuracy
  }
}
