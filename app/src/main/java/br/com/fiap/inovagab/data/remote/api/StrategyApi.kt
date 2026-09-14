package br.com.fiap.inovagab.data.remote.api

import br.com.fiap.inovagab.data.remote.dto.StrategyDto
import br.com.fiap.inovagab.data.remote.dto.StrategyHistoryDto
import br.com.fiap.inovagab.data.remote.dto.StrategyRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StrategyApi {

    @GET("api/strategies")
    suspend fun list(@Query("activeOnly") activeOnly: Boolean? = null): List<StrategyDto>

    @GET("api/strategies/{id}")
    suspend fun getById(@Path("id") id: String): StrategyDto

    @POST("api/strategies")
    suspend fun create(@Body body: StrategyRequestDto): StrategyDto

    @PUT("api/strategies/{id}")
    suspend fun update(@Path("id") id: String, @Body body: StrategyRequestDto): StrategyDto

    @DELETE("api/strategies/{id}")
    suspend fun delete(@Path("id") id: String)

    @GET("api/strategies/{id}/history")
    suspend fun history(@Path("id") id: String): List<StrategyHistoryDto>
}
