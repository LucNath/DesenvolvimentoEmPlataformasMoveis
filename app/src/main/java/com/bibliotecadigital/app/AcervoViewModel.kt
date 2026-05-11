package com.bibliotecadigital.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AcervoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _allBooks = MutableLiveData<List<Book>>(emptyList())
    
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _selectedCategory = MutableLiveData("Todas")
    val selectedCategory: LiveData<String> = _selectedCategory

    val categories: LiveData<List<String>> = _allBooks.map { books ->
        val list = books.map { it.category }.distinct().sorted().toMutableList()
        list.add(0, "Todas")
        list
    }

    val mostBorrowedBooks: LiveData<List<Book>> = _allBooks.map { books ->
        books.filter { it.isMostBorrowed }
    }

    val filteredBooks: LiveData<List<Book>> = androidx.lifecycle.MediatorLiveData<List<Book>>().apply {
        fun update() {
            val query = _searchQuery.value ?: ""
            val category = _selectedCategory.value ?: "Todas"
            val books = _allBooks.value ?: emptyList()

            value = books.filter { book ->
                val matchesQuery = book.title.contains(query, ignoreCase = true) ||
                        book.author.contains(query, ignoreCase = true) ||
                        book.isbn.contains(query, ignoreCase = true)
                
                val matchesCategory = category == "Todas" || book.category == category
                
                matchesQuery && matchesCategory
            }
        }

        addSource(_allBooks) { update() }
        addSource(_searchQuery) { update() }
        addSource(_selectedCategory) { update() }
    }

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("books").get().await()
                val books = snapshot.documents.mapNotNull { doc ->
                    // Converter documento Firestore para objeto Book
                    // Para simplificar, assumimos que os campos batem ou usamos mapeamento manual
                    val book = doc.toObject(Book::class.java)
                    book?.copy(id = doc.id)
                }
                _allBooks.value = books
                
                // Se o Firestore estiver vazio, podemos popular com dados iniciais (opcional)
                if (books.isEmpty()) {
                    seedDatabase()
                }
            } catch (e: Exception) {
                // Log error or update UI state
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