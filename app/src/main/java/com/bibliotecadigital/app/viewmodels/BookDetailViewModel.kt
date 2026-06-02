package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.entity.Book
import com.bibliotecadigital.app.repository.BookRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

import com.bibliotecadigital.app.repository.LoanRepository
import com.google.firebase.auth.FirebaseAuth

class BookDetailViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val bookRepository = BookRepository(db)
    private val loanRepository = LoanRepository(db)
    private val auth = FirebaseAuth.getInstance()

    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> = _book

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _borrowResult = MutableLiveData<Result<String>>()
    val borrowResult: LiveData<Result<String>> = _borrowResult

    private val _reserveResult = MutableLiveData<Result<Unit>>()
    val reserveResult: LiveData<Result<Unit>> = _reserveResult

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

    fun borrowBook(book: Book) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = loanRepository.createLoan(
                userId = userId,
                bookId = book.id,
                title = book.title,
                author = book.author,
                coverUrl = book.coverUrl
            )
            _borrowResult.value = result
            if (result.isSuccess) {
                loadBook(book.id)
            }
            _isLoading.value = false
        }
    }

    private val reservationRepository = com.bibliotecadigital.app.repository.ReservationRepository(db)

    fun reserveBook(book: Book) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = reservationRepository.createReservation(
                userId, book.id, book.title, book.author, book.coverUrl
            )
            _reserveResult.value = result
            if (result.isSuccess) {
                loadBook(book.id)
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