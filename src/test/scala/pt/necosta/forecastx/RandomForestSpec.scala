package pt.necosta.forecastx

import java.io.File

class RandomForestSpec extends TestConfig {

  "RandomForest" should "prepare data" in {
    val sourceFilePath = this.getClass
      .getResource("/sourceData.csv")
      .getFile

    val sourceDs = DataPrep
      .withConfig(new File(sourceFilePath).getParent)
      .transformSourceFile()

    val outDs = DataForecast.prepData(sourceDs)

    outDs.count() should be(40)

    // ToDo: add tests
    //RandomForest.start(outDs)
  }
}
