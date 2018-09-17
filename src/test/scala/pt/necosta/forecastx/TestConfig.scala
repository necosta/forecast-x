package pt.necosta.forecastx

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

trait TestConfig
    extends FlatSpec
    with Matchers
    with BeforeAndAfterAll
    with SharedSparkContext {}
