package br.com.fiap.inovagab.data.remote.dto

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    val token: String = "",
    val tokenType: String = "Bearer",
    val expiresInMs: Long = 0,
    val user: UserDto = UserDto()
)

data class UserDto(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val points: Int = 0
)
