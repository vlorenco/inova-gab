package br.com.fiap.inovagab.data.model

/** Orientacao estrategica publicada pela lideranca. */
data class Strategy(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val category: String = "",
    val campaign: String = "",
    val active: Boolean = true
)

data class StrategyHistoryEntry(
    val id: String = "",
    val strategyId: String = "",
    val title: String = "",
    val description: String = "",
    val action: String = "",
    val changedAt: String = "",
    val active: Boolean = true
)
