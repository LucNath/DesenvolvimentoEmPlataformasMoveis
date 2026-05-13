package com.bibliotecadigital.app

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private fun getCollection(userId: String) = 
        firestore.collection("users").document(userId).collection("notifications")

    fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val subscription = getCollection(userId)
            .orderBy("id", Query.Direction.DESCENDING) // Usando id como fallback se não houver timestamp real
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Notification::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(notifications)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun markAsRead(userId: String, notificationId: String): Result<Unit> = try {
        getCollection(userId).document(notificationId).update("isRead", true).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun markAllAsRead(userId: String): Result<Unit> = try {
        val batch = firestore.batch()
        val notifications = getCollection(userId).whereEqualTo("isRead", false).get().await()
        for (doc in notifications) {
            batch.update(doc.reference, "isRead", true)
        }
        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteNotification(userId: String, notificationId: String): Result<Unit> = try {
        getCollection(userId).document(notificationId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
