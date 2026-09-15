package br.com.fiap.inovagab

import android.app.Application
import br.com.fiap.inovagab.data.remote.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Inicializa a camada de rede (Retrofit + TokenManager) uma unica vez e
 * recarrega o JWT salvo do DataStore para a copia em memoria que o
 * AuthInterceptor le de forma sincrona.
 *
 * O app sempre abre pela tela de login: o token recarregado aqui nao pula essa
 * etapa, ele so evita que uma chamada disparada logo apos o boot saia sem
 * header Authorization.
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
