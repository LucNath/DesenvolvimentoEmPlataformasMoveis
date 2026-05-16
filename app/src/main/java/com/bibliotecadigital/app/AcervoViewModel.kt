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

    private val bookRepository = BookRepository(FirebaseFirestore.getInstance())
    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val categories: StateFlow<List<String>> = _allBooks.map { books ->
        val list = books.map { it.category }.distinct().filter { it.isNotEmpty() }.sorted().toMutableList()
        if (!list.contains("Todas")) list.add(0, "Todas")
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
        observeBooks()
    }

    private fun observeBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                bookRepository.getBooks().collect { books ->
                    _allBooks.value = books
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                android.util.Log.e("AcervoViewModel", "Error observing books", e)
                _allBooks.value = emptyList()
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }
}
