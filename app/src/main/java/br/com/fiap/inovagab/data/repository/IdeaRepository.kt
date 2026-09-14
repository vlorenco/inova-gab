package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.AiAnalysis
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.remote.ApiClient
import br.com.fiap.inovagab.data.remote.api.IdeaApi
import br.com.fiap.inovagab.data.remote.apiCall
import br.com.fiap.inovagab.data.remote.dto.IdeaPriorityRequestDto
import br.com.fiap.inovagab.data.remote.dto.IdeaRequestDto
import br.com.fiap.inovagab.data.remote.dto.IdeaStatusRequestDto
import br.com.fiap.inovagab.data.remote.dto.toDomain

class IdeaRepository(
    private val api: IdeaApi = ApiClient.ideaApi
) {

    /** O backend define operatorId/operatorName a partir do JWT. */
    suspend fun createIdea(
        title: String,
        problem: String,
        solution: String,
        area: String,
        benefit: String,
        strategyId: String?
    ): Result<Idea> = apiCall {
        api.create(
            IdeaRequestDto(
                title = title,
                problem = problem,
                solution = solution,
                area = area.ifBlank { null },
                benefit = benefit.ifBlank { null },
                strategyId = strategyId?.ifBlank { null }
            )
        ).toDomain()
    }

    suspend fun getMyIdeas(): Result<List<Idea>> =
        apiCall { api.myIdeas().map { it.toDomain() } }

    suspend fun getAllIdeas(status: String? = null): Result<List<Idea>> =
        apiCall { api.listAll(status).map { it.toDomain() } }

    suspend fun getIdea(ideaId: String): Result<Idea> =
        apiCall { api.getById(ideaId).toDomain() }

    suspend fun updateIdea(
        ideaId: String,
        title: String,
        problem: String,
        solution: String,
        area: String,
        benefit: String,
        strategyId: String?
    ): Result<Idea> = apiCall {
        api.update(
            ideaId,
            IdeaRequestDto(
                title = title,
                problem = problem,
                solution = solution,
                area = area.ifBlank { null },
                benefit = benefit.ifBlank { null },
                strategyId = strategyId?.ifBlank { null }
            )
        ).toDomain()
    }

    suspend fun deleteIdea(ideaId: String): Result<Unit> =
        apiCall { api.delete(ideaId) }

    suspend fun updateIdeaPriority(ideaId: String, priority: String): Result<Idea> =
        apiCall { api.updatePriority(ideaId, IdeaPriorityRequestDto(priority)).toDomain() }

    suspend fun updateIdeaStatus(ideaId: String, status: String): Result<Idea> =
        apiCall { api.updateStatus(ideaId, IdeaStatusRequestDto(status)).toDomain() }

    /** Dispara a analise da ideia pelo Gemini (o backend fala com a IA). */
    suspend fun requestAiAnalysis(ideaId: String): Result<AiAnalysis> =
        apiCall { api.aiAnalysis(ideaId).toDomain() }
}
