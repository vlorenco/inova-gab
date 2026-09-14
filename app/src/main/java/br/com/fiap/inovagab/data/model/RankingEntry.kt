package br.com.fiap.inovagab.data.model

data class RankingEntry(
    val position: Int = 0,
    val userId: String = "",
    val name: String = "",
    val points: Int = 0
)

data class MyRanking(
    val position: Int = 0,
    val totalOperators: Int = 0,
    val name: String = "",
    val points: Int = 0
)
