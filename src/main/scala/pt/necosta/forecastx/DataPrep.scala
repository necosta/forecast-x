package pt.necosta.forecastx

import java.io.File
import java.net.URL

import sys.process._
import org.apache.spark.sql.Dataset

import scala.language.postfixOps

object DataPrep {

  def withConfig(sourceFolder: String): DataPrep = {
    new DataPrep(sourceFolder)
  }
}

class DataPrep(sourceFolder: String) extends WithSpark {

  val sourceFilePath = s"$sourceFolder/sourceData.csv"

  def runImport(): Unit = {
    val urlHost = "raw.githubusercontent.com"
    val urlPath = "JeffSackmann/tennis_atp/master"

    new URL(s"https://$urlHost/$urlPath/atp_matches_2017.csv") #> new File(
      sourceFilePath) !!
  }

  def transformSourceFile(): Dataset[InputRecord] = {
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
