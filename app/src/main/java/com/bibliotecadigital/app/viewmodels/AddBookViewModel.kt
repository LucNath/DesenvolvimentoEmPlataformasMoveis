package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddBookViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _saveSuccess = MutableSharedFlow<String>()
    val saveSuccess: SharedFlow<String> = _saveSuccess

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    fun saveBook(
        bookId: String?,
        title: String,
        author: String,
        category: String,
        isbn: String,
        quantity: Int,
        year: Int,
        coverUrl: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bookMap = mutableMapOf(
                    "title" to title,
                    "author" to author,
                    "category" to category,
                    "isbn" to isbn,
                    "quantity" to quantity,
                    "year" to year,
                    "coverUrl" to coverUrl,
                    "searchKeywords" to listOf(title.lowercase(), author.lowercase(), category.lowercase())
                )

                if (bookId == null) {
                    // Novo livro: disponibilidade inicial = quantidade total
                    bookMap["availableQuantity"] = quantity
                    db.collection("books").add(bookMap).await()
                    _saveSuccess.emit("Obra cadastrada com sucesso!")
                } else {
                    // Edição: ajustar disponibilidade mantendo livros emprestados
                    val currentBook = db.collection("books").document(bookId).get().await()
                    val oldTotal = currentBook.getLong("quantity") ?: 0
                    val oldAvailable = currentBook.getLong("availableQuantity") ?: 0
                    val borrowed = oldTotal - oldAvailable
                    
                    val newAvailable = (quantity - borrowed).coerceAtLeast(0)
                    bookMap["availableQuantity"] = newAvailable
                    
                    db.collection("books").document(bookId).update(bookMap as Map<String, Any>).await()
                    _saveSuccess.emit("Obra atualizada com sucesso!")
                }
            } catch (e: Exception) {
                _error.emit("Erro ao salvar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
