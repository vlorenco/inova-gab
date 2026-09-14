package br.com.fiap.inovagab.data.remote.api

import br.com.fiap.inovagab.data.remote.dto.AiAnalysisDto
import br.com.fiap.inovagab.data.remote.dto.IdeaDto
import br.com.fiap.inovagab.data.remote.dto.IdeaPriorityRequestDto
import br.com.fiap.inovagab.data.remote.dto.IdeaRequestDto
import br.com.fiap.inovagab.data.remote.dto.IdeaStatusRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IdeaApi {

    @POST("api/ideas")
    suspend fun create(@Body body: IdeaRequestDto): IdeaDto

    @GET("api/ideas/my")
    suspend fun myIdeas(): List<IdeaDto>

    @GET("api/ideas")
    suspend fun listAll(@Query("status") status: String? = null): List<IdeaDto>

    @GET("api/ideas/{id}")
    suspend fun getById(@Path("id") id: String): IdeaDto

    @PUT("api/ideas/{id}")
    suspend fun update(@Path("id") id: String, @Body body: IdeaRequestDto): IdeaDto

    @DELETE("api/ideas/{id}")
    suspend fun delete(@Path("id") id: String)

    @PATCH("api/ideas/{id}/priority")
    suspend fun updatePriority(@Path("id") id: String, @Body body: IdeaPriorityRequestDto): IdeaDto

    @PATCH("api/ideas/{id}/status")
    suspend fun updateStatus(@Path("id") id: String, @Body body: IdeaStatusRequestDto): IdeaDto

    @POST("api/ideas/{id}/ai-analysis")
    suspend fun aiAnalysis(@Path("id") id: String): AiAnalysisDto
}
