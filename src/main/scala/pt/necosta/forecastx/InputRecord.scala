package pt.necosta.forecastx

case class InputRecord(tourneyId: String, // Tournament Id. Ex: 2018-M020
                       tourneyName: String, // Tournament Name. Ex: Brisbane
                       surface: Option[String], // Ex: Hard, Clay, Grass
                       drawSize: Int,
                       tourneyLevel: String, // Ex: A,B,C
                       tourneyDate: Int,
                       matchNum: Int,
                       winnerId: Int, // Ex: 1,2,3
                       winnerSeed: Option[Int],
                       winnerEntry: String,
                       winnerName: String,
                       winnerHand: Option[String], // Ex: R,L, U
                       winnerHt: Option[Int], // Ex: 183
                       winnerIoc: String,
                       winnerAge: Double,
                       winnerRank: Int,
                       winnerRankPoints: Int,
                       loserId: Int,
                       loserSeed: Option[Int],
                       loserEntry: String,
                       loserName: String,
                       loserHand: Option[String], // Ex: R,L, U
                       loserHt: Option[Int],
                       loserIoc: String,
                       loserAge: Double,
                       loserRank: Int,
                       loserRankPoints: Int,
                       score: String, // Ex. "7-6(3) 5-7 6-4"
                       bestOf: Int,
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
