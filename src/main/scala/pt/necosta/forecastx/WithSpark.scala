package pt.necosta.forecastx

import org.apache.spark.sql.SparkSession

trait WithSpark {

  implicit lazy val spark: SparkSession = SparkSession.builder().getOrCreate()
}
