package pt.necosta.forecastx

import org.apache.spark.sql.SparkSession

object StartMode extends Enumeration {
  type StartMode = Value
  val Import, Stats, Analysis, Forecast, Score = Value
}

object ForecastX {
  def main(args: Array[String]): Unit = {
    println("Starting Spark application")

    val spark = SparkSession.builder
      .appName("ForecastX")
      .getOrCreate()

    val sourceFolder = sys.env.getOrElse("SPARK_SOURCE_FOLDER", "/forecastx")

    val dataflow = Dataflow.withConfig(sourceFolder)

    import StartMode._
    if (args.length > 0) {
      val arg1 = args(0)
      withNameOption(arg1) match {
        case Some(Import)   => dataflow.startImport()
        case Some(Stats)    => dataflow.startStatsCollection()
        case Some(Analysis) => dataflow.startAnalysis()
        case Some(Forecast) => dataflow.startForecast()
        case Some(Score)    => dataflow.startScoring()
        case _ =>
          println(
            s"Invalid argument: $arg1. Valid values: ${StartMode.values.mkString(",")}")
      }
    } else {
      dataflow.startImport()
      dataflow.startStatsCollection()
      dataflow.startAnalysis()
      dataflow.startForecast()
    }
    spark.stop()
  }

  private def withNameOption(name: String): Option[StartMode.Value] =
    try {
      Some(StartMode.withName(name))
    } catch {
      case _: NoSuchElementException => None
    }
}
