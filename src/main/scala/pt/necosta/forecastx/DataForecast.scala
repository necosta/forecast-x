package pt.necosta.forecastx

import org.apache.spark.sql.{DataFrame, Dataset}
import pt.necosta.forecastx.record.{InputRecord, RandomForestRecord}
import org.apache.spark.sql.functions._

object DataForecast extends WithSpark {

  def prepData(sourceDs: Dataset[InputRecord]): Dataset[RandomForestRecord] = {
    import spark.implicits._

    val winnerDs = sourceDs
      .select(
        lit(1.0).as("isWinner"),
        col("surface"),
        col("drawSize"),
        col("tourneyLevel"),
        col("winnerSeed").alias("seed"),
        col("winnerEntry").alias("entry"),
        col("winnerHand").alias("hand"),
        col("winnerHt").alias("height"),
        col("winnerIOC").alias("country"),
        col("winnerAge").alias("age"),
        col("winnerRank").alias("rank"),
        col("winnerRankPoints").alias("rankPoints"),
        col("round"),
        col("score")
      )

    val loserDs = sourceDs
      .select(
        lit(0.0).as("isWinner"),
        col("surface"),
        col("drawSize"),
        col("tourneyLevel"),
        col("loserSeed").alias("seed"),
        col("loserEntry").alias("entry"),
        col("loserHand").alias("hand"),
        col("loserHt").alias("height"),
        col("loserIOC").alias("country"),
        col("loserAge").alias("age"),
        col("loserRank").alias("rank"),
        col("loserRankPoints").alias("rankPoints"),
        col("round"),
        col("score")
      )

    val out = winnerDs
      .union(loserDs)
      .fillNa[String]("surface", "Hard")
      .fillNa[Int]("seed", 999)
      .fillNa[String]("entry", "N")
      .fillNa[Int]("height", 175)
      .fillNa[String]("hand", "U")
      .fillNa[Int]("rank", 999)
      .fillNa[Int]("rankPoints", 0)
      .fillNa[Int]("age", 30)
      .as[RandomForestRecord]
    //out.show(10)
    out
  }

  protected implicit class fillNa(df: DataFrame) {
    def fillNa[T](columnName: String, defaultValue: T): DataFrame = {
      defaultValue match {
        case _: Int =>
          df.na.fill(defaultValue.asInstanceOf[Int], Seq(columnName))
        case _: Double =>
          df.na.fill(defaultValue.asInstanceOf[Double], Seq(columnName))
        case _ => df.na.fill(defaultValue.asInstanceOf[String], Seq(columnName))
      }
    }
  }
}
