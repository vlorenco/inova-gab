package br.com.fiap.inovagab.data.remote

import android.content.Context
import br.com.fiap.inovagab.BuildConfig
import br.com.fiap.inovagab.data.remote.api.AuthApi
import br.com.fiap.inovagab.data.remote.api.DashboardApi
import br.com.fiap.inovagab.data.remote.api.IdeaApi
import br.com.fiap.inovagab.data.remote.api.ProjectApi
import br.com.fiap.inovagab.data.remote.api.RankingApi
import br.com.fiap.inovagab.data.remote.api.StrategyApi
import br.com.fiap.inovagab.data.remote.interceptor.AuthInterceptor
import br.com.fiap.inovagab.data.session.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Ponto unico de acesso ao backend.
 *
 * A URL base vem do BuildConfig (definida em app/build.gradle.kts), entao nao
 * existe endereco fixo espalhado pelas telas.
 */
object ApiClient {

    /** Ex.: http://10.0.2.2:8080/ no Android Emulator. */
    val baseUrl: String get() = BuildConfig.API_BASE_URL

    lateinit var tokenManager: TokenManager
        private set

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        if (::retrofit.isInitialized) return

        tokenManager = TokenManager(context.applicationContext)

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val strategyApi: StrategyApi by lazy { retrofit.create(StrategyApi::class.java) }
    val ideaApi: IdeaApi by lazy { retrofit.create(IdeaApi::class.java) }
    val projectApi: ProjectApi by lazy { retrofit.create(ProjectApi::class.java) }
    val dashboardApi: DashboardApi by lazy { retrofit.create(DashboardApi::class.java) }
    val rankingApi: RankingApi by lazy { retrofit.create(RankingApi::class.java) }
}
