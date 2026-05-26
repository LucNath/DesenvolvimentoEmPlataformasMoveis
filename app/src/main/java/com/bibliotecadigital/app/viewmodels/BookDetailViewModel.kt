package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.entity.Book
import com.bibliotecadigital.app.repository.BookRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class BookDetailViewModel : ViewModel() {

    private val bookRepository = BookRepository(FirebaseFirestore.getInstance())
    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> = _book

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            bookRepository.getBookById(bookId).onSuccess {
                _book.value = it
            }.onFailure {
                // Handle error
            }
            _isLoading.value = false
        }
    }

    fun updateBookAvailability(bookId: String, delta: Int) {
        viewModelScope.launch {
            bookRepository.updateAvailability(bookId, delta).onSuccess {
                loadBook(bookId) // Reload to get updated data
            }
        }
    }
}