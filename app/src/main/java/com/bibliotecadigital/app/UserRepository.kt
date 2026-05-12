package com.bibliotecadigital.app

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val usersCollection = firestore.collection("users")

    suspend fun createUser(user: User): Result<Unit> = try {
        usersCollection.document(user.uid).set(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUser(uid: String): Result<User> = try {
        val document = usersCollection.document(uid).get().await()
        val user = document.toObject(User::class.java)
        if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Usuário não encontrado"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateUser(uid: String, fields: Map<String, Any>): Result<Unit> = try {
        usersCollection.document(uid).update(fields).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteUser(uid: String): Result<Unit> = try {
        usersCollection.document(uid).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}