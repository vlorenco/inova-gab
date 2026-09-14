package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.Project
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LeaderProjectRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("projects")

    suspend fun getProjects(): Result<List<Project>> = runCatching {
        collection.get().await().documents.mapNotNull {
            it.toObject(Project::class.java)?.copy(id = it.id)
        }
    }
}
