package pt.necosta.forecastx

import java.io.File

import org.apache.spark.sql.SparkSession

class DataPrepSpec extends TestConfig {

  "DataPrep" should "correctly import source file as dataset" in {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val sourceFilePath = this.getClass
      .getResource("/sourceData.csv")
      .getFile

    val sourceDS = DataPrep
      .withConfig(new File(sourceFilePath).getParent)
      .transformSourceFile()

    sourceDS.count() should be(11)

    sourceDS.map(_.tourneyId).collect().distinct should be(Array("2018-M020"))

    sourceDS.map(_.tourneyLevel).collect().distinct should be(Array("A"))

    sourceDS.map(_.winnerHand).collect().distinct should be(Array("R"))

    sourceDS.map(_.winnerAge).collect().max should be < 30.0
  }
}
