package com.bibliotecadigital.app

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun signUp(user: User, senha: String): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.email, senha).await()
            val uid = result.user?.uid ?: throw Exception("Falha ao obter UID")
            
            // Salva dados adicionais no Firestore
            db.collection("users").document(uid).set(user.copy(uid = uid)).await()
            
            Result.success(true)
        } catch (e: FirebaseAuthException) {
            Log.e("AuthRepository", "signUp error: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepository", "signUp general error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, senha: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, senha).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: FirebaseAuthException) {
            Log.e("AuthRepository", "signIn error: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepository", "signIn general error: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: FirebaseAuthException) {
            Log.e("AuthRepository", "resetPassword error: ${e.errorCode} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepository", "resetPassword general error: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }
}
