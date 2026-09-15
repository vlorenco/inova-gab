package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.User
import br.com.fiap.inovagab.data.remote.ApiClient
import br.com.fiap.inovagab.data.remote.apiCall
import br.com.fiap.inovagab.data.remote.dto.LoginRequestDto
import br.com.fiap.inovagab.data.remote.dto.toDomain
import br.com.fiap.inovagab.data.remote.api.AuthApi
import br.com.fiap.inovagab.data.session.TokenManager

/**
 * Autenticacao contra o backend Spring Boot.
 * Antes: Firebase Auth + Firestore. Agora: POST /api/auth/login + JWT local.
 */
class AuthRepository(
    private val api: AuthApi = ApiClient.authApi,
    private val tokenManager: TokenManager = ApiClient.tokenManager
) {

    suspend fun login(email: String, password: String): Result<User> =
        apiCall { api.login(LoginRequestDto(email, password)) }
            .map { response ->
                val user = response.user.toDomain()
                tokenManager.save(response.token, user.role)
                user
            }

    /** Perfil do dono do token (GET /api/auth/me). */
    suspend fun currentUser(): Result<User> =
        apiCall { api.me().toDomain() }

    suspend fun logout() {
        tokenManager.clear()
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun currentRole(): String? = tokenManager.currentRole()
}
