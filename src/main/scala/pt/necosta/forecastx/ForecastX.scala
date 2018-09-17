package pt.necosta.forecastx

import org.apache.spark.sql.SparkSession

object ForecastX {
  def main(args: Array[String]): Unit = {
    println("Starting Spark application")

    val spark = SparkSession.builder
      .appName("ForecastX")
      .getOrCreate()

    val sourceFolder = sys.env.getOrElse("SPARK_SOURCE_FOLDER", "/forecastx")

    val dataflow = Dataflow.withConfig(sourceFolder)

    println("Starting data import")
    dataflow.runImport()

    println("Starting data transformation")
    dataflow.transformSourceFile()

    println("TODO: Starting data analysis")
    //dataflow.runAnalysis()

    spark.stop()

  }
}
