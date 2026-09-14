package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.MyRanking
import br.com.fiap.inovagab.data.model.RankingEntry
import br.com.fiap.inovagab.data.remote.ApiClient
import br.com.fiap.inovagab.data.remote.api.RankingApi
import br.com.fiap.inovagab.data.remote.apiCall
import br.com.fiap.inovagab.data.remote.dto.toDomain

/**
 * Ranking dos operadores. As regras de pontuacao ficam no backend
 * (criar ideia +10, ideia aprovada +50, ideia virou projeto +100).
 */
class RankingRepository(
    private val api: RankingApi = ApiClient.rankingApi
) {

    suspend fun getRanking(): Result<List<RankingEntry>> =
        apiCall { api.ranking().map { it.toDomain() } }

    suspend fun getMyPosition(): Result<MyRanking> =
        apiCall { api.myPosition().toDomain() }
}
