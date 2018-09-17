package pt.necosta.forecastx

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.count

case class TournamentCount(tourneyId: String, tourneyCount: BigInt)

object DataAnalysis extends WithSpark {

  def getTournamentCount: Dataset[InputRecord] => Dataset[TournamentCount] = {
    import spark.implicits._

    ds =>
      ds.groupBy($"tourneyId")
        .agg(count($"tourneyId").alias("tourneyCount"))
        .as[TournamentCount]
  }
}
