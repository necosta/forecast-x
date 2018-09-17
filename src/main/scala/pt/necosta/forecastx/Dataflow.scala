package pt.necosta.forecastx

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
    val outputDs = inputDs.transform(DataAnalysis.getTournamentCount)

    val tournamentCountDs = outputDs
      .orderBy(desc("tourneyCount"))
      .take(NUMBER_RECORDS)

    println(s"\nThe top $NUMBER_RECORDS tournaments with more games:\n")
    tournamentCountDs.foreach(r =>
      println(s"${r.tourneyId}: ${r.tourneyCount} games"))
  }

  def startValidation(): Unit = ???

  def startForecast(): Unit = ???
}
