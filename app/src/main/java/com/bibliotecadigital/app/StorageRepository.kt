package com.bibliotecadigital.app

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageRepository(private val storage: FirebaseStorage) {

    suspend fun uploadBookCover(bookId: String, uri: Uri): Result<String> = runCatching {
        val ref = storage.reference.child("book_covers/$bookId.jpg")
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }

    suspend fun deleteBookCover(bookId: String): Result<Unit> = runCatching {
        val ref = storage.reference.child("book_covers/$bookId.jpg")
        ref.delete().await()
    }
}