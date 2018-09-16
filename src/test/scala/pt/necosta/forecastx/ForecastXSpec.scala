package pt.necosta.forecastx

import org.scalatest.{FlatSpec, Matchers}

class ForecastXSpec extends FlatSpec with Matchers {

  "Forecast" should "check add operation" in {
    ForecastX.add(3,4) should be(7)
  }
}
