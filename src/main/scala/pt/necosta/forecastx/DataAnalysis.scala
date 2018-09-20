package pt.necosta.forecastx

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._
import pt.necosta.forecastx.record._

object DataAnalysis extends WithSpark {

  def getTournamentGamesCount: Dataset[InputRecord] => Dataset[GamesCount] = {
    import spark.implicits._

    ds =>
      ds.groupBy($"tourneyId", $"tourneyName")
        .agg(count($"tourneyId").alias("tourneyCount"))
        .as[GamesCount]
  }

  def getSurfaceDistribution
    : Dataset[InputRecord] => Dataset[SurfaceDistribution] = {
    import spark.implicits._

    ds =>
      {
        val total = ds.count
        ds.groupBy($"surface")
          .agg(count(lit(1)).alias("surfaceCount"))
          .withColumn("fraction", col("surfaceCount") * 100 / total)
          .as[SurfaceDistribution]
      }
  }

  def getHandDistribution: Dataset[InputRecord] => Dataset[HandDistribution] = {
    import spark.implicits._

    ds =>
      {
        val total = ds.count
        ds.groupBy($"winnerHand", $"loserHand")
          .agg(count(lit(1)).alias("count"))
          .withColumn("fraction", col("count") * 100 / total)
          .as[HandDistribution]
      }
  }
}
