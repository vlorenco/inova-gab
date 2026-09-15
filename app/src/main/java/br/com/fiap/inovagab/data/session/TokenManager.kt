package br.com.fiap.inovagab.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "inovagab_session")

/**
 * Guarda o JWT no DataStore do app.
 *
 * O interceptor precisa do token de forma sincrona, entao mantemos tambem uma
 * copia em memoria que e carregada no boot e atualizada a cada login/logout.
 */
class TokenManager(private val context: Context) {

    @Volatile
    private var cachedToken: String? = null

    @Volatile
    private var cachedRole: String? = null

    suspend fun load() {
        cachedToken = context.dataStore.data.map { it[KEY_TOKEN] }.first()
        cachedRole = context.dataStore.data.map { it[KEY_ROLE] }.first()
    }

    suspend fun save(token: String, role: String) {
        cachedToken = token
        cachedRole = role
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_ROLE] = role
        }
    }

    suspend fun clear() {
        cachedToken = null
        cachedRole = null
        context.dataStore.edit { it.clear() }
    }

    /** Usado pelo AuthInterceptor. */
    fun currentToken(): String? = cachedToken

    fun currentRole(): String? = cachedRole

    fun isLoggedIn(): Boolean = !cachedToken.isNullOrBlank()

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("jwt_token")
        val KEY_ROLE = stringPreferencesKey("user_role")
    }
}
