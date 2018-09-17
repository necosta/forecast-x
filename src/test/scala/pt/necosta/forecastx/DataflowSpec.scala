package pt.necosta.forecastx

import org.apache.spark.sql.SparkSession

class DataflowSpec extends TestConfig {

  "Dataflow" should "correctly import source file as dataset" in {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val sourceFilePath = this.getClass
      .getResource("/sourceData.csv")
      .getFile

    val outputDS = Dataflow
      .withoutConfig()
      .transformSourceFile(sourceFilePath)

    outputDS.count() should be(3)

    outputDS.map(_.value).collect() should be(Array(1, 10, 100))
  }
}
