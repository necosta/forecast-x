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

    sourceDS.count() should be(20)

    sourceDS.map(_.tourneyId).collect().distinct should be(
      Array("2018-M020", "2018-7161", "2018-0425"))

    sourceDS.map(_.tourneyLevel).collect().distinct should be(Array("A", "B"))

    sourceDS.map(_.winnerHand).collect().distinct should be(
      Array(Some("L"), Some("R"), Some("U")))

    sourceDS.map(_.winnerAge).collect().max should be < 32.0
  }
}
