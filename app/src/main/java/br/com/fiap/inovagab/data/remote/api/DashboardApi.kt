package br.com.fiap.inovagab.data.remote.api

import br.com.fiap.inovagab.data.remote.dto.DashboardSummaryDto
import br.com.fiap.inovagab.data.remote.dto.ProjectDashboardDto
import br.com.fiap.inovagab.data.remote.dto.StrategyDashboardDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DashboardApi {

    @GET("api/dashboard/summary")
    suspend fun summary(): DashboardSummaryDto

    @GET("api/dashboard/strategies/{strategyId}")
    suspend fun byStrategy(@Path("strategyId") strategyId: String): StrategyDashboardDto

    @GET("api/dashboard/projects/{projectId}")
    suspend fun byProject(@Path("projectId") projectId: String): ProjectDashboardDto
}
