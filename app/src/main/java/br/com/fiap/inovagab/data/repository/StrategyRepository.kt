package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.Strategy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StrategyRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("strategies")

    suspend fun getStrategies(): Result<List<Strategy>> = runCatching {
        collection.get().await().documents.mapNotNull {
            it.toObject(Strategy::class.java)?.copy(id = it.id)
        }
    }

    suspend fun createStrategy(strategy: Strategy): Result<String> = runCatching {
        val doc = collection.add(strategy).await()
        doc.id
    }

    suspend fun deleteStrategy(strategyId: String): Result<Unit> = runCatching {
        collection.document(strategyId).delete().await()
    }

    suspend fun updateStrategy(strategy: Strategy): Result<Unit> = runCatching {
        collection.document(strategy.id).set(strategy).await()
    }
}
