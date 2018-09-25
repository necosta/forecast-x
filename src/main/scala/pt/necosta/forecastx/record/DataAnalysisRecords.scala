package pt.necosta.forecastx.record

case class GamesCount(tourneyId: String,
                      tourneyName: String,
                      tourneyCount: BigInt)

case class SurfaceDistribution(surface: String, percentage: Double)

case class HandDistribution(winnerHand: String,
                            loserHand: String,
                            percentage: Double)
