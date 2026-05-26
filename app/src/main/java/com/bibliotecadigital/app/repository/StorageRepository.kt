package com.bibliotecadigital.app.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * TASK-BE11: Firebase Storage — Upload de Capa de Livros
 *
 * Regras do Firebase Storage:
 * // rules_version = '2';
 * // service firebase.storage {
 * //   match /b/{bucket}/o {
 * //     match /books/{bookId}/cover.jpg {
 * //       allow read: if true;
 * //       allow write: if request.auth != null && request.auth.token.role == 'admin';
 * //     }
 * //   }
 * // }
 */
class StorageRepository(
    private val storage: FirebaseStorage,
    private val context: Context
) {

    private val storageRef = storage.reference

    suspend fun uploadCover(bookId: String, imageUri: Uri): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val ref = storageRef.child("books/$bookId/cover.jpg")

                // Compressão da imagem para no máximo 512KB
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: throw Exception("Não foi possível abrir a imagem")

                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                var quality = 100
                var byteArray: ByteArray
                val outputStream = ByteArrayOutputStream()

                do {
                    outputStream.reset()
                    originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    byteArray = outputStream.toByteArray()
                    quality -= 5
                } while (byteArray.size > 512 * 1024 && quality > 5)

                ref.putBytes(byteArray).await()
                ref.downloadUrl.await().toString()
            }
        }

    suspend fun getCoverUrl(bookId: String): Result<String> = runCatching {
        val ref = storageRef.child("books/$bookId/cover.jpg")
        ref.downloadUrl.await().toString()
    }

    suspend fun deleteCover(bookId: String): Result<Unit> = runCatching {
        val ref = storageRef.child("books/$bookId/cover.jpg")
        ref.delete().await()
    }
}