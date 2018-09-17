package pt.necosta.forecastx

import org.apache.spark.sql.Dataset

object Dataflow {

  def withConfig(sourceFolder: String): Dataflow = {
    new Dataflow(Some(sourceFolder))
  }

  def withoutConfig(): Dataflow = {
    new Dataflow(None)
  }
}

class Dataflow(sourceFolder: Option[String]) extends WithSpark {

  def transformSourceFile(sourceFilePath: String): Dataset[InputRecord] = {
    import spark.implicits._

    spark.read
      .option("inferSchema", "true")
      .option("header", "true")
      .csv(sourceFilePath)
      .as[InputRecord]
  }
}
