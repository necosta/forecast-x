package pt.necosta.forecastx.record

case class RandomForestRecord(isWinner: Double, // label we want to predict
                              surface: String,
                              drawSize: Int,
                              tourneyLevel: String,
                              seed: Int,
                              entry: String,
                              hand: String,
                              height: Int,
                              country: String,
                              age: Double,
                              rank: Int,
                              rankPoints: Int,
                              round: String)
