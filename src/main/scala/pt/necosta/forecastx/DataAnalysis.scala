package pt.necosta.forecastx

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._

object DataAnalysis extends WithSpark {

  def getTournamentGamesCount: Dataset[InputRecord] => Dataset[GamesCount] = {
    import spark.implicits._

    ds =>
      ds.groupBy($"tourneyId", $"tourneyName")
        .agg(count($"tourneyId").alias("tourneyCount"))
        .as[GamesCount]
  }

  def getTournamentSurfaceDistribution
    : Dataset[InputRecord] => Dataset[SurfaceDistribution] = {
    import spark.implicits._

    ds =>
      {
        val total = ds.count
        ds.groupBy($"surface")
          .agg(count($"surface").alias("surfaceCount"))
          .withColumn("fraction", col("surfaceCount") * 100 / total)
          .as[SurfaceDistribution]
      }
  }
}
