package br.com.fiap.inovagab.data.remote.interceptor

import br.com.fiap.inovagab.data.session.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Anexa o JWT em todas as chamadas, exceto no proprio login.
 * Header: Authorization: Bearer TOKEN
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath.endsWith("/api/auth/login")) {
            return chain.proceed(request)
        }

        val token = tokenManager.currentToken()
        val authenticated = if (token.isNullOrBlank()) {
            request
        } else {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(authenticated)
    }
}
