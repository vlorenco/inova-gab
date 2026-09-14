package br.com.fiap.inovagab.data.repositoryfirestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import br.com.fiap.inovagab.data.model.Operador

class RankingService {
    private val db = FirebaseFirestore.getInstance()
    private val colecao = db.collection("operadores")

    suspend fun adicionarPontos(operadorId: String, pontos: Int): Boolean {
        return try {
            val operadorRef = colecao.document(operadorId)
            operadorRef.update("pontos", com.google.firebase.firestore.FieldValue.increment(pontos.toLong())).await()
            true
        } catch (e: Exception) {
            Log.e("RankingService", "Erro ao adicionar pontos", e)
            false
        }
    }

    suspend fun getRanking(): List<Operador> {
        return try {
            val snapshot = colecao
                .orderBy("pontos", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject<Operador>()?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("RankingService", "Erro ao buscar ranking", e)
            emptyList()
        }
    }

    suspend fun getPontosOperador(operadorId: String): Int {
        return try {
            val doc = colecao.document(operadorId).get().await()
            doc.getLong("pontos")?.toInt() ?: 0
        } catch (e: Exception) {
            Log.e("RankingService", "Erro ao buscar pontos", e)
            0
        }
    }
}