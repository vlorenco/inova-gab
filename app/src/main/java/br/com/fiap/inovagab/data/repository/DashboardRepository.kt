package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.DashboardSummary
import br.com.fiap.inovagab.data.remote.ApiClient
import br.com.fiap.inovagab.data.remote.api.DashboardApi
import br.com.fiap.inovagab.data.remote.apiCall
import br.com.fiap.inovagab.data.remote.dto.ProjectDashboardDto
import br.com.fiap.inovagab.data.remote.dto.StrategyDashboardDto
import br.com.fiap.inovagab.data.remote.dto.toDomain

/** Indicadores da lideranca, ja calculados pelo backend (inclusive o ROI). */
class DashboardRepository(
    private val api: DashboardApi = ApiClient.dashboardApi
) {

    suspend fun getSummary(): Result<DashboardSummary> =
        apiCall { api.summary().toDomain() }

    suspend fun getByStrategy(strategyId: String): Result<StrategyDashboardDto> =
        apiCall { api.byStrategy(strategyId) }

    suspend fun getByProject(projectId: String): Result<ProjectDashboardDto> =
        apiCall { api.byProject(projectId) }
}
