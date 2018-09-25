package pt.necosta.forecastx.record

case class InputRecord(tourneyId: String, // Tournament Id. Ex: 2018-M020
                       tourneyName: String, // Tournament Name. Ex: Brisbane
                       surface: Option[String], // Ex: Hard, Clay, Grass
                       drawSize: Int, // Ex: 32, 64, 128
                       tourneyLevel: String, // Ex: A, B, C
                       tourneyDate: Int, // Ex: 20180423
                       matchNum: Int, // Ex. 1, 5, 188
                       winnerId: Int, // Ex: 1, 2, 3
                       winnerSeed: Option[Int], // Ex: 1, 10, 50
                       winnerEntry: Option[String], // Ex: Q, WC
                       winnerName: String,
                       winnerHand: Option[String], // Ex: R, L, U
                       winnerHt: Option[Int], // Winner Height. Ex: 183
                       winnerIoc: String, // Winner Country. Ex: AUS
                       winnerAge: Double,
                       winnerRank: Int,
                       winnerRankPoints: Int,
                       loserId: Int,
                       loserSeed: Option[Int],
                       loserEntry: Option[String],
                       loserName: String,
                       loserHand: Option[String],
                       loserHt: Option[Int],
                       loserIoc: String,
                       loserAge: Double,
                       loserRank: Int,
                       loserRankPoints: Int,
                       score: String, // Ex. "7-6(3) 5-7 6-4"
                       bestOf: Int, // Best of 3 or 5 games
                       round: String, // Ex. R16
                       minutes: Int,
                       wAce: Int,
                       wDf: Int,
                       wSvpt: Int,
                       w1stIn: Int,
                       w1stWon: Int,
                       w2ndWon: Int,
                       wSvGms: Int,
                       wBpSaved: Int,
                       wBpFaced: Int,
                       lAce: Int,
                       lDf: Int,
                       lSvpt: Int,
                       l1stIn: Int,
                       l1stWon: Int,
                       l2ndWon: Int,
                       lSvGms: Int,
                       lBpSaved: Int,
                       lBpFaced: Int)
