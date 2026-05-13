package com.bibliotecadigital.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AcervoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val categories: StateFlow<List<String>> = _allBooks.map { books ->
        val list = books.map { it.category }.distinct().sorted().toMutableList()
        list.add(0, "Todas")
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Todas"))

    val mostBorrowedBooks: StateFlow<List<Book>> = _allBooks.map { books ->
        books.filter { it.isMostBorrowed }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredBooks: StateFlow<List<Book>> = combine(_allBooks, _searchQuery, _selectedCategory) { books, query, category ->
        books.filter { book ->
            val matchesQuery = book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true) ||
                    book.isbn.contains(query, ignoreCase = true)
            
            val matchesCategory = category == "Todas" || book.category == category
            
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("books").get().await()
                val books = snapshot.documents.mapNotNull { doc ->
                    val book = doc.toObject(Book::class.java)
                    book?.copy(id = doc.id)
                }
                _allBooks.value = books
                
                if (books.isEmpty()) {
                    seedDatabase()
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    private suspend fun seedDatabase() {
        val mockBooks = listOf(
            Book("1", "O Senhor dos Anéis", "J.R.R. Tolkien", "Literatura", BookStatus.AVAILABLE, "https://m.media-amazon.com/images/I/81hCVY69p8L.jpg", "9780007136582", isMostBorrowed = true),
            Book("2", "Direito Civil Contemporâneo", "Tartuce", "Direito", BookStatus.BORROWED, "https://m.media-amazon.com/images/I/71D0Y7tG20L.jpg", "9788530983635"),
            Book("3", "Cálculo A", "Diva Flemming", "Engenharia", BookStatus.AVAILABLE, "https://m.media-amazon.com/images/I/81S6ZIn2QSL.jpg", "9788576051152"),
            Book("4", "1984", "George Orwell", "Literatura", BookStatus.RESERVED, "https://m.media-amazon.com/images/I/71kxa1-0mfL.jpg", "9780451524935", isMostBorrowed = true),
            Book("5", "Princípios de Engenharia", "Holtzapple", "Engenharia", BookStatus.AVAILABLE, "https://m.media-amazon.com/images/I/41D8W5S+1zL.jpg", "9788521615439"),
            Book("6", "Dom Casmurro", "Machado de Assis", "Literatura", BookStatus.AVAILABLE, "https://m.media-amazon.com/images/I/81p8OqL9rAL.jpg", "9788508044304"),
            Book("7", "Código Penal Comentado", "Nucci", "Direito", BookStatus.AVAILABLE, "https://m.media-amazon.com/images/I/71Yy367Bw8L.jpg", "9788530983635", isMostBorrowed = true)
        )
        
        mockBooks.forEach { book ->
            db.collection("books").document(book.id).set(book).await()
        }
        _allBooks.value = mockBooks
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }
}
