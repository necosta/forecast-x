package pt.necosta.forecastx

import java.io.File

import org.apache.spark.sql.Dataset
import pt.necosta.forecastx.record.InputRecord

class DataForecastSpec extends TestConfig {

  "DataForecast" should "correctly transform input data" in {

    val outputDS = DataForecast.prepData(getSourceDs)

    outputDS.head().isWinner should be(1.0)
    outputDS.head().surface should be("Hard")
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
