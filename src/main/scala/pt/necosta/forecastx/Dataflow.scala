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

    val df = spark.read
      .option("inferSchema", "true")
      .option("header", "true")
      .csv(sourceFilePath)

    // Remove underscore from all column names
    df.columns
      .foldLeft(df)((curr, n) =>
        curr.withColumnRenamed(n, n.replaceAll("_", "")))
      .as[InputRecord]
  }
}
