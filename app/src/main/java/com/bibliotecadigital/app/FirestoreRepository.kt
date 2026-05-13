package com.bibliotecadigital.app

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

open class FirestoreRepository(private val db: FirebaseFirestore) {

    suspend fun <T> getDocument(collection: String, id: String, clazz: Class<T>): Result<T> = runCatching {
        val snapshot = db.collection(collection).document(id).get().await()
        snapshot.toObject(clazz) ?: throw Exception("Document not found")
    }

    suspend fun setDocument(collection: String, id: String, data: Any): Result<Unit> = runCatching {
        db.collection(collection).document(id).set(data).await()
    }

    suspend fun updateDocument(collection: String, id: String, fields: Map<String, Any>): Result<Unit> = runCatching {
        db.collection(collection).document(id).update(fields).await()
    }

    suspend fun deleteDocument(collection: String, id: String): Result<Unit> = runCatching {
        db.collection(collection).document(id).delete().await()
    }

    fun <T : Any> getCollection(collection: String, clazz: Class<T>): Flow<List<T>> = callbackFlow {
        val registration = db.collection(collection).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.toObjects(clazz))
            }
        }
        awaitClose { registration.remove() }
    }

    fun <T : Any> getFilteredCollection(
        collection: String,
        clazz: Class<T>,
        vararg filters: Pair<String, Any>
    ): Flow<List<T>> = callbackFlow {
        var query = db.collection(collection) as com.google.firebase.firestore.Query
        filters.forEach { (field, value) ->
            query = query.whereEqualTo(field, value)
        }
        
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.toObjects(clazz))
            }
        }
        awaitClose { registration.remove() }
    }
}