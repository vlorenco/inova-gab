package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.model.StrategyHistoryEntry
import br.com.fiap.inovagab.data.remote.ApiClient
import br.com.fiap.inovagab.data.remote.api.StrategyApi
import br.com.fiap.inovagab.data.remote.apiCall
import br.com.fiap.inovagab.data.remote.dto.toDomain
import br.com.fiap.inovagab.data.remote.dto.toRequest

class StrategyRepository(
    private val api: StrategyApi = ApiClient.strategyApi
) {

    suspend fun getStrategies(activeOnly: Boolean? = null): Result<List<Strategy>> =
        apiCall { api.list(activeOnly).map { it.toDomain() } }

    suspend fun createStrategy(strategy: Strategy): Result<Strategy> =
        apiCall { api.create(strategy.toRequest()).toDomain() }

    suspend fun updateStrategy(strategy: Strategy): Result<Strategy> =
        apiCall { api.update(strategy.id, strategy.toRequest()).toDomain() }

    suspend fun deleteStrategy(strategyId: String): Result<Unit> =
        apiCall { api.delete(strategyId) }

    suspend fun getHistory(strategyId: String): Result<List<StrategyHistoryEntry>> =
        apiCall { api.history(strategyId).map { it.toDomain() } }
}
