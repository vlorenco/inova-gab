package br.com.fiap.inovagab.data.repository

import br.com.fiap.inovagab.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("Usuário não encontrado."))

            val doc = firestore.collection("users").document(uid).get().await()

            if (!doc.exists()) {
                return Result.failure(Exception("Perfil do usuário não encontrado."))
            }

            val role = doc.getString("role")
                ?: return Result.failure(Exception("Campo 'role' não encontrado no perfil do usuário."))

            if (role.isBlank()) {
                return Result.failure(Exception("Campo 'role' está vazio no perfil do usuário."))
            }

            val user = User(
                id = uid,
                name = doc.getString("name") ?: "",
                email = doc.getString("email") ?: "",
                role = role
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
