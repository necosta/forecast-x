package pt.necosta.forecastx

import java.io.File

import org.apache.spark.sql.{Dataset, SparkSession}

class DataAnalysisSpec extends TestConfig {

  "DataAnalysis" should "correctly get number of games per tournament" in {
    val outputDS = getSourceDs.transform(DataAnalysis.getTournamentGamesCount)

    outputDS.head().tourneyId should be("2018-M020")
    outputDS.head().tourneyCount should be(11)
  }

  "DataAnalysis" should "correctly get surface distribution" in {
    val outputDS = getSourceDs.transform(DataAnalysis.getSurfaceDistribution)

    outputDS.head().surface should be("Hard")
    outputDS.head().fraction should be(100.0)
  }

  "DataAnalysis" should "correctly get hand win/lost distribution" in {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val outputDS = getSourceDs.transform(DataAnalysis.getHandDistribution)

    outputDS.count() should be(2)
    outputDS.map(_.loserHand).collect() should be(Array("R", "L"))
    outputDS.map(_.fraction).collect().sum should be(100.0)
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
