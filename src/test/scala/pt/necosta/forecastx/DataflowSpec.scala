package pt.necosta.forecastx

import java.io.File

import org.apache.spark.sql.SparkSession

class DataflowSpec extends TestConfig {

  "Dataflow" should "correctly import source file as dataset" in {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val sourceFilePath = this.getClass
      .getResource("/sourceData.csv")
      .getFile

    val outputDS = Dataflow
      .withConfig(new File(sourceFilePath).getParent)
      .transformSourceFile()

    outputDS.count() should be(11)

    outputDS.map(_.tourneyId).collect().distinct should be(Array("2018-M020"))

    outputDS.map(_.tourneyLevel).collect().distinct should be(Array("A"))

    outputDS.map(_.winnerHand).collect().distinct should be(Array("R"))

    outputDS.map(_.winnerAge).collect().max should be < 30.0
  }
}
