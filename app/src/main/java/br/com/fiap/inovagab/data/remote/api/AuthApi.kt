package br.com.fiap.inovagab.data.remote.api

import br.com.fiap.inovagab.data.remote.dto.LoginRequestDto
import br.com.fiap.inovagab.data.remote.dto.LoginResponseDto
import br.com.fiap.inovagab.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @GET("api/auth/me")
    suspend fun me(): UserDto
}
