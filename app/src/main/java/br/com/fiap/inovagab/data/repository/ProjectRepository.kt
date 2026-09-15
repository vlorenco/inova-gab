package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.remote.ApiClient
import br.com.fiap.inovagab.data.remote.api.ProjectApi
import br.com.fiap.inovagab.data.remote.apiCall
import br.com.fiap.inovagab.data.remote.dto.toDomain
import br.com.fiap.inovagab.data.remote.dto.toRequest

/**
 * Projetos e iniciativas. Atende tanto o gestor (CRUD) quanto a lideranca
 * (consulta) - as permissoes reais sao aplicadas pelo backend.
 */
class ProjectRepository(
    private val api: ProjectApi = ApiClient.projectApi
) {

    suspend fun getProjects(): Result<List<Project>> =
        apiCall { api.list().map { it.toDomain() } }

    suspend fun getProject(projectId: String): Result<Project> =
        apiCall { api.getById(projectId).toDomain() }

    suspend fun createProject(project: Project): Result<Project> =
        apiCall { api.create(project.toRequest()).toDomain() }

    suspend fun updateProject(project: Project): Result<Project> =
        apiCall { api.update(project.id, project.toRequest()).toDomain() }

    suspend fun deleteProject(projectId: String): Result<Unit> =
        apiCall { api.delete(projectId) }
}
