package pt.necosta.forecastx.record

case class RandomForestRecord(isWinner: Double,
                              surface: String, // Ex: Hard, Clay, Grass
                              drawSize: Int,
                              tourneyLevel: String, // Ex: A,B,C
                              seed: Int,
                              entry: String,
                              hand: String, // Ex: R,L, U
                              ioc: String,
                              age: Double,
                              rank: Int,
                              rankPoints: Int)
//ToDo: Add more features
//ace: Int,
//df: Int,
//svpt: Int,
//1stIn: Int,
//1stWon: Int,
//2ndWon: Int,
//svGms: Int,
//bpSaved: Int,
//bpFaced: Int)
