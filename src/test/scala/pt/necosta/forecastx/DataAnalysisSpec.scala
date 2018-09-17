package pt.necosta.forecastx

import java.io.File

class DataAnalysisSpec extends TestConfig {

  "DataAnalysis" should "correctly get number of games per tournament" in {
    val sourceFilePath = this.getClass
      .getResource("/sourceData.csv")
      .getFile

    val sourceDS = DataPrep
      .withConfig(new File(sourceFilePath).getParent)
      .transformSourceFile()

    val outputDS = sourceDS.transform(DataAnalysis.getTournamentGamesCount)

    outputDS.head().tourneyId should be("2018-M020")
    outputDS.head().tourneyCount should be(11)
  }

  "DataAnalysis" should "correctly get surface distribution" in {
    val sourceFilePath = this.getClass
      .getResource("/sourceData.csv")
      .getFile

    val sourceDS = DataPrep
      .withConfig(new File(sourceFilePath).getParent)
      .transformSourceFile()

    val outputDS =
      sourceDS.transform(DataAnalysis.getTournamentSurfaceDistribution)

    outputDS.head().surface should be("Hard")
    outputDS.head().fraction should be(100.0)
  }
}
