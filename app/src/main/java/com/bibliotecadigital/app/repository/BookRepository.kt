package com.bibliotecadigital.app.repository

import com.bibliotecadigital.app.repository.FirestoreRepository
import com.bibliotecadigital.app.entity.Book
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class BookRepository(private val db: FirebaseFirestore) : FirestoreRepository(db) {

    private val booksCollection = "books"

    fun getBooks(limit: Long = 20): Flow<List<Book>> {
        return getCollection(booksCollection, Book::class.java)
    }

    fun getBooksByCategory(category: String): Flow<List<Book>> {
        return getFilteredCollection(booksCollection, Book::class.java, "category" to category)
    }

    suspend fun searchBooks(queryText: String): Result<List<Book>> = runCatching {
        // O Firestore não suporta busca textual nativa (Full-text search) de forma eficiente sem serviços externos.
        // Implementamos uma busca por prefixo simples no título para fins de MVP.
        val snapshot = db.collection(booksCollection)
            .whereGreaterThanOrEqualTo("title", queryText)
            .whereLessThanOrEqualTo("title", queryText + "\uf8ff")
            .get()
            .await()
        snapshot.toObjects(Book::class.java)
    }

    suspend fun getBookById(bookId: String): Result<Book> {
        return getDocument(booksCollection, bookId, Book::class.java)
    }

    suspend fun addBook(book: Book): Result<String> = runCatching {
        val docRef = db.collection(booksCollection).document()
        val bookWithId = book.copy(id = docRef.id)
        docRef.set(bookWithId).await()
        docRef.id
    }

    suspend fun updateBook(book: Book): Result<Unit> = runCatching {
        setDocument(booksCollection, book.id, book).getOrThrow()
    }

    suspend fun deleteBook(bookId: String): Result<Unit> = runCatching {
        deleteDocument(booksCollection, bookId).getOrThrow()
    }

    suspend fun updateAvailability(bookId: String, delta: Int): Result<Unit> = runCatching {
        db.runTransaction { transaction ->
            val ref = db.collection(booksCollection).document(bookId)
            val snapshot = transaction.get(ref)

            val currentAvailable = snapshot.getLong("available") ?: 0
            val newAvailable = currentAvailable + delta

            if (newAvailable < 0) throw Exception("Quantidade indisponível")

            transaction.update(ref, "available", newAvailable)

            // Opcional: Atualizar status automaticamente
            val newStatus = if (newAvailable > 0) "available" else "unavailable"
            transaction.update(ref, "status", newStatus)
        }.await()
    }

    // Exemplo de listagem paginada conforme solicitado
    suspend fun getBooksPaginated(limit: Long, lastDocument: DocumentSnapshot?): Result<Pair<List<Book>, DocumentSnapshot?>> = runCatching {
        var query = db.collection(booksCollection)
            .orderBy("title")
            .limit(limit)

        lastDocument?.let {
            query = query.startAfter(it)
        }

        val snapshot = query.get().await()
        val books = snapshot.toObjects(Book::class.java)
        val lastDoc = if (snapshot.documents.isNotEmpty()) snapshot.documents.last() else null

        Pair(books, lastDoc)
    }
}