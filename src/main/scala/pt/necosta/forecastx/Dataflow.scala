package pt.necosta.forecastx

import java.io.File

import org.apache.spark.sql.functions._
import pt.necosta.forecastx.record.InputRecord

object Dataflow {

  def withConfig(sourceFolder: String): Dataflow = {
    new Dataflow(sourceFolder)
  }
}

class Dataflow(sourceFolder: String) extends WithSpark {

  private val SAVE_FORMAT = "parquet"
  val dataPrep: DataPrep = DataPrep.withConfig(sourceFolder)
  val outputFile = s"$sourceFolder/output.parquet"
  val modelFile = s"$sourceFolder/rf_model"

  def startImport(): Unit = {

    if (new File(outputFile).exists()) {
      println("Skipping data import. File exists.")
      return
    }

    println("Starting data raw import")
    dataPrep.runImport()

    println("Starting data transformation")
    val outputDs = dataPrep.transformSourceFile()

    println("Persist dataset as parquet")
    outputDs.write.format(SAVE_FORMAT).save(outputFile)
  }

  def startAnalysis(): Unit = {
    import spark.implicits._

    val NUMBER_RECORDS = 3

    val inputDs = spark.read.parquet(outputFile).as[InputRecord]

    println("Starting data analysis")
    val tournamentCountDs = inputDs
      .transform(DataAnalysis.getTournamentGamesCount)
      .orderBy(desc("tourneyCount"))
      .take(NUMBER_RECORDS)

    // ToDo: Normalize null vs None
    val surfaceDistributionDs = inputDs
      .transform(DataAnalysis.getSurfaceDistribution)
      .orderBy(desc("percentage"))
      .collect()

    val handDistributionDs = inputDs
      .transform(DataAnalysis.getHandDistribution)
      .orderBy(desc("percentage"))
      .collect()

    println(s"\nThe top $NUMBER_RECORDS tournaments with more games:\n")
    tournamentCountDs.foreach(r =>
      println(s"${r.tourneyId}-${r.tourneyName}: ${r.tourneyCount} games"))

    println(s"\nTournaments surface distribution:\n")
    surfaceDistributionDs.foreach(r =>
      println(s"${r.surface}: ${r.percentage}"))

    println(s"\nTournaments hand winning distribution:\n")
    handDistributionDs.foreach(
      r =>
        println(
          s"Winner:${r.winnerHand} - Loser:${r.loserHand} => ${r.percentage}"))
  }

  def startStatsCollection(): Unit = {
    import spark.implicits._

    val inputDs = spark.read.parquet(outputFile).as[InputRecord]

    println("Starting data validation")

    val rows = inputDs.count()
    val columns = inputDs.columns.length
    val years = inputDs.groupBy($"tourneyDate".substr(0, 4)).count().count()
    val tournaments = inputDs.groupBy($"tourneyId").count().count()

    println(s"Number of rows: $rows")

    println(s"Number of columns: $columns")

    println(s"Number of years: $years")

    println(s"Number of tournaments: $tournaments")
  }

  def startForecast(): Unit = {
    import spark.implicits._

    if (new File(modelFile).exists()) {
      println("Skipping forecasting. Model exists.")
      return
    }

    val inputDs = spark.read.parquet(outputFile).as[InputRecord]

    println("Starting data forecasting")

    val model = RandomForest.start(DataForecast.prepData(inputDs))

    RandomForest.save(model, modelFile)
  }
}
