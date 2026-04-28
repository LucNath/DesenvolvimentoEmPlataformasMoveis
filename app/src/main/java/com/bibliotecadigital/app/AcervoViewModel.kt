package com.bibliotecadigital.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AcervoViewModel : ViewModel() {

    private val _allBooks = MutableLiveData<List<Book>>()
    
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
        loadMockData()
    }

    private fun loadMockData() {
        val mockBooks = listOf(
            Book("1", "O Senhor dos Anéis", "J.R.R. Tolkien", "Literatura", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder, "9780007136582", isMostBorrowed = true),
            Book("2", "Direito Civil Contemporâneo", "Tartuce", "Direito", BookStatus.BORROWED, R.drawable.bg_cover_placeholder, "9788530983635"),
            Book("3", "Cálculo A", "Diva Flemming", "Engenharia", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder, "9788576051152"),
            Book("4", "1984", "George Orwell", "Literatura", BookStatus.RESERVED, R.drawable.bg_cover_placeholder, "9780451524935", isMostBorrowed = true),
            Book("5", "Princípios de Engenharia", "Holtzapple", "Engenharia", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder, "9788521615439"),
            Book("6", "Dom Casmurro", "Machado de Assis", "Literatura", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder, "9788508044304"),
            Book("7", "Código Penal Comentado", "Nucci", "Direito", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder, "9788530983635", isMostBorrowed = true)
        )
        _allBooks.value = mockBooks
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }
}