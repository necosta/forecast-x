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

  def runImport(): Unit = {
    val urlHost = "raw.githubusercontent.com"
    val urlPath = "JeffSackmann/tennis_atp/master"

    // Import last 10 years of data
    (0 to 10).foreach(i => {
      val year = 2018 - i
      new URL(s"https://$urlHost/$urlPath/atp_matches_$year.csv") #> new File(
        s"$sourceFolder/sourceData_$year.csv") !!
    })
  }

  def transformSourceFile(): Dataset[InputRecord] = {
    import spark.implicits._

    val df = spark.read
      .option("inferSchema", "true")
      .option("header", "true")
      .csv(s"$sourceFolder/sourceData*.csv")

    // Remove underscore from all column names
    df.columns
      .foldLeft(df)((curr, n) =>
        curr.withColumnRenamed(n, n.replaceAll("_", "")))
      .as[InputRecord]
  }
}
