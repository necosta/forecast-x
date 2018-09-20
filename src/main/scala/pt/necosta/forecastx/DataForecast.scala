package pt.necosta.forecastx

import org.apache.spark.sql.Dataset
import pt.necosta.forecastx.record.{InputRecord, RandomForestRecord}
import org.apache.spark.sql.functions.{col, _}

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
        col("winnerIOC").alias("ioc"),
        col("winnerAge").alias("age"),
        col("winnerRank").alias("rank"),
        col("winnerRankPoints").alias("rankPoints")
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
        col("loserIOC").alias("ioc"),
        col("loserAge").alias("age"),
        col("loserRank").alias("rank"),
        col("loserRankPoints").alias("rankPoints")
      )

    winnerDs
      .union(loserDs)
      .as[RandomForestRecord]
  }
}
