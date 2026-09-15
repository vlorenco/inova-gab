package br.com.fiap.inovagab.data.remote.dto

data class StrategyDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String? = null,
    val category: String? = null,
    val campaign: String? = null,
    val active: Boolean = true,
    val createdBy: String? = null
)

data class StrategyRequestDto(
    val title: String,
    val description: String,
    val date: String? = null,
    val category: String? = null,
    val campaign: String? = null,
    val active: Boolean = true
)

data class StrategyHistoryDto(
    val id: String = "",
    val strategyId: String = "",
    val title: String = "",
    val description: String = "",
    val date: String? = null,
    val category: String? = null,
    val campaign: String? = null,
    val active: Boolean = true,
    val action: String = "",
    val changedAt: String? = null,
    val changedBy: String? = null
)
