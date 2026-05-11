package com.bibliotecadigital.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun cadastrar(user: User, senha: String): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.email, senha).await()
            val uid = result.user?.uid ?: throw Exception("Falha ao obter UID")
            
            // Salva dados adicionais no Firestore
            db.collection("users").document(uid).set(user.copy(id = uid)).await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, senha: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, senha).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }
}
