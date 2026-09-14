package br.com.fiap.inovagab.data.remote.api

import br.com.fiap.inovagab.data.remote.dto.MyRankingDto
import br.com.fiap.inovagab.data.remote.dto.RankingEntryDto
import retrofit2.http.GET

interface RankingApi {

    @GET("api/ranking")
    suspend fun ranking(): List<RankingEntryDto>

    @GET("api/ranking/me")
    suspend fun myPosition(): MyRankingDto
}
