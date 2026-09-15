package br.com.fiap.inovagab.data.remote.dto

data class RankingEntryDto(
    val position: Int = 0,
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val points: Int = 0
)

data class MyRankingDto(
    val position: Int = 0,
    val totalOperators: Int = 0,
    val userId: String = "",
    val name: String = "",
    val points: Int = 0
)
