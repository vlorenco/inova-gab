package br.com.fiap.inovagab.data.remote

import br.com.fiap.inovagab.data.remote.dto.ApiErrorDto
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

/**
 * Executa uma chamada da API convertendo qualquer falha em uma mensagem que faz
 * sentido para o usuario, em vez de vazar stacktrace para a tela.
 */
suspend fun <T> apiCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: HttpException) {
        Result.failure(Exception(friendlyMessage(e)))
    } catch (e: IOException) {
        Result.failure(
            Exception("Nao foi possivel conectar ao servidor. Verifique se o backend esta rodando em ${ApiClient.baseUrl}")
        )
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Erro inesperado ao comunicar com o servidor."))
    }

private fun friendlyMessage(e: HttpException): String {
    val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
    val parsed = body?.let { runCatching { Gson().fromJson(it, ApiErrorDto::class.java) }.getOrNull() }

    parsed?.details?.takeIf { it.isNotEmpty() }?.let { return it.joinToString("\n") }
    parsed?.message?.takeIf { it.isNotBlank() }?.let { return it }

    return when (e.code()) {
        401 -> "Sessao expirada. Faca login novamente."
        403 -> "Seu perfil nao tem permissao para esta acao."
        404 -> "Registro nao encontrado."
        409 -> "Esta operacao conflita com um registro existente."
        503 -> "Servico temporariamente indisponivel."
        else -> "Erro ${e.code()} ao comunicar com o servidor."
    }
}
