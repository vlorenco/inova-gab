package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.Idea
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class IdeaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("ideas")

    suspend fun createIdea(idea: Idea): Result<String> = runCatching {
        val doc = collection.add(idea).await()
        doc.id
    }

    suspend fun getAllIdeas(): Result<List<Idea>> = runCatching {
        collection.orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await().documents.mapNotNull {
                it.toObject(Idea::class.java)?.copy(id = it.id)
            }
    }

    suspend fun getIdeasByOperator(operatorId: String): Result<List<Idea>> = runCatching {
        collection.whereEqualTo("operatorId", operatorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await().documents.mapNotNull {
                it.toObject(Idea::class.java)?.copy(id = it.id)
            }
    }

    suspend fun updateIdeaStatus(ideaId: String, status: String): Result<Unit> = runCatching {
        val updates = mutableMapOf<String, Any>("status" to status)
        if (status == "APROVADA") {
            updates["approvedAt"] = System.currentTimeMillis()
        }
        collection.document(ideaId).update(updates).await()
    }

    suspend fun updateIdeaPriority(ideaId: String, priority: String): Result<Unit> = runCatching {
        collection.document(ideaId).update("priority", priority).await()
    }

    suspend fun markConvertedToProject(ideaId: String): Result<Unit> = runCatching {
        collection.document(ideaId).update("convertedToProject", true).await()
    }
}
