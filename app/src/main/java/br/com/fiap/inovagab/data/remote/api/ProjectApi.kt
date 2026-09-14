package br.com.fiap.inovagab.data.remote.api

import br.com.fiap.inovagab.data.remote.dto.ProjectDto
import br.com.fiap.inovagab.data.remote.dto.ProjectRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProjectApi {

    @GET("api/projects")
    suspend fun list(): List<ProjectDto>

    @GET("api/projects/{id}")
    suspend fun getById(@Path("id") id: String): ProjectDto

    @POST("api/projects")
    suspend fun create(@Body body: ProjectRequestDto): ProjectDto

    @PUT("api/projects/{id}")
    suspend fun update(@Path("id") id: String, @Body body: ProjectRequestDto): ProjectDto

    @DELETE("api/projects/{id}")
    suspend fun delete(@Path("id") id: String)
}
