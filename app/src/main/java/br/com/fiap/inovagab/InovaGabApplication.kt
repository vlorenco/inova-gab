package br.com.fiap.inovagab

import android.app.Application
import br.com.fiap.inovagab.data.remote.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Inicializa a camada de rede (Retrofit + TokenManager) uma unica vez e
 * recarrega o JWT salvo, para que o app volte autenticado apos ser reaberto.
 */
class InovaGabApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
        CoroutineScope(Dispatchers.IO).launch {
            ApiClient.tokenManager.load()
        }
    }
}
