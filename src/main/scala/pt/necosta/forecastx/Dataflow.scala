package pt.necosta.forecastx

import java.io.File

import org.apache.spark.sql.functions._

object Dataflow {

  def withConfig(sourceFolder: String): Dataflow = {
    new Dataflow(sourceFolder)
  }
}

class Dataflow(sourceFolder: String) extends WithSpark {

  private val SAVE_FORMAT = "parquet"
  val dataPrep: DataPrep = DataPrep.withConfig(sourceFolder)
  val outputFile = s"$sourceFolder/output.parquet"

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

    val surfaceDistributionDs = inputDs
      .transform(DataAnalysis.getTournamentSurfaceDistribution)
      .orderBy(desc("fraction"))
      .collect()

    println(s"\nThe top $NUMBER_RECORDS tournaments with more games:\n")
    tournamentCountDs.foreach(r =>
      println(s"${r.tourneyId}-${r.tourneyName}: ${r.tourneyCount} games"))

    println(s"\nTournaments surface distribution:\n")
    surfaceDistributionDs.foreach(r => println(s"${r.surface}: ${r.fraction}"))
  }

  def startValidation(): Unit = {
    import spark.implicits._

    val inputDs = spark.read.parquet(outputFile).as[InputRecord]

    println("Starting data validation")

    println(s"Number of rows: ${inputDs.count()}")

    println(s"Number of columns: ${inputDs.columns.length}")

  }

  def startForecast(): Unit = ???
}
