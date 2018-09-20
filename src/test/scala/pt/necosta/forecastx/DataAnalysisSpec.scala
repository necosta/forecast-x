package pt.necosta.forecastx

import java.io.File

import org.apache.spark.sql.{Dataset, SparkSession}
import pt.necosta.forecastx.record.InputRecord
import org.apache.spark.sql.functions._

class DataAnalysisSpec extends TestConfig {

  "DataAnalysis" should "correctly get number of games per tournament" in {
    val outputDS = getSourceDs.transform(DataAnalysis.getTournamentGamesCount)

    outputDS.count() should be(3)

    outputDS
      .filter(col("tourneyId") === "2018-M020")
      .head()
      .tourneyCount should be(11)
  }

  "DataAnalysis" should "correctly get surface distribution" in {
    val outputDS = getSourceDs.transform(DataAnalysis.getSurfaceDistribution)

    outputDS.count() should be(3)

    outputDS
      .filter(col("surface") === "Hard")
      .head()
      .fraction should be(50.0)
  }

  "DataAnalysis" should "correctly get hand win/lost distribution" in {
    val outputDS = getSourceDs.transform(DataAnalysis.getHandDistribution)

    outputDS.count() should be(5)

    outputDS
      .filter(col("winnerHand") === "R" && col("loserHand") === "L")
      .head()
      .fraction should be(10.0)
  }

  private def getSourceDs: Dataset[InputRecord] = {
    val sourceFilePath = this.getClass
      .getResource("/sourceData.csv")
      .getFile

    DataPrep
      .withConfig(new File(sourceFilePath).getParent)
      .transformSourceFile()
  }
}
