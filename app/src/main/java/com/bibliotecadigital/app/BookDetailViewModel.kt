package com.bibliotecadigital.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookDetailViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> = _book

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("books").document(bookId).get().await()
                val book = snapshot.toObject(Book::class.java)
                if (book != null) {
                    _book.value = book.copy(id = snapshot.id)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
