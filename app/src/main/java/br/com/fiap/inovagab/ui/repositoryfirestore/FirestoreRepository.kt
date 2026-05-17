package br.com.fiap.inovagab.ui.repositoryfirestore

import br.com.fiap.inovagab.ui.operador.estrategias.model.Estrategia
import br.com.fiap.inovagab.ui.operador.model.Ideia
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun salvarIdeia(ideia: Ideia) {
        db.collection("ideias")
            .add(
                ideia.copy(
                    dataCriacao = Timestamp.now()
                )
            )
            .await()
    }

    suspend fun buscarIdeias(): List<Ideia> {
        return db.collection("ideias")
            .get()
            .await()
            .documents
            .mapNotNull {
                it.toObject(Ideia::class.java)?.copy(id = it.id)
            }
    }

    suspend fun buscarEstrategias(): List<Estrategia> {
        return db.collection("estrategias")
            .get()
            .await()
            .documents
            .mapNotNull {
                it.toObject(Estrategia::class.java)?.copy(id = it.id)
            }
    }
}