package pt.necosta.forecastx.record

case class GamesCount(tourneyId: String,
                      tourneyName: String,
                      tourneyCount: BigInt)

case class SurfaceDistribution(surface: String, fraction: Double)

case class HandDistribution(winnerHand: String,
                            loserHand: String,
                            fraction: Double)
