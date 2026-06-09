package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.entity.Book
import com.bibliotecadigital.app.repository.BookRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val bookRepository = BookRepository(db)

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _actionMessage = MutableSharedFlow<String>()
    val actionMessage: SharedFlow<String> = _actionMessage

    val filteredBooks = combine(_books, _searchQuery) { books, query ->
        if (query.isEmpty()) {
            books
        } else {
            books.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.author.contains(query, ignoreCase = true)
            }
        }
    }

    fun loadBooks(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            bookRepository.getBooksByCategory(category).collect { bookList ->
                _books.value = bookList
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun handleBookAction(book: Book) {
        viewModelScope.launch {
            if (book.availableQuantity > 0) {
                // Aqui no futuro chamaremos o repositório de empréstimos
                _actionMessage.emit("Reserva realizada com sucesso para: ${book.title}")
            } else {
                _actionMessage.emit("Obra indisponível para reserva no momento.")
            }
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            bookRepository.deleteBook(bookId)
                .onSuccess {
                    _actionMessage.emit("Obra excluída com sucesso")
                }
                .onFailure {
                    _actionMessage.emit("Erro ao excluir: ${it.message ?: "Erro desconhecido"}")
                }
            _isLoading.value = false
        }
    }
}
