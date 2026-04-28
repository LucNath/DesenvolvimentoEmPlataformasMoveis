package com.bibliotecadigital.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookDetailViewModel : ViewModel() {

    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> = _book

    fun loadBook(bookId: String) {
        // In a real app, this would come from a Repository. 
        // For now, we use mock data.
        val mockBook = Book(
            id = bookId,
            title = "O Senhor dos Anéis",
            author = "J.R.R. Tolkien",
            category = "Literatura",
            status = BookStatus.AVAILABLE,
            coverRes = R.drawable.bg_cover_placeholder,
            isbn = "9780007136582",
            publisher = "HarperCollins",
            year = "1954",
            synopsis = "O Senhor dos Anéis é uma trilogia de livros de alta fantasia escrita pelo escritor, filólogo e professor universitário britânico J. R. R. Tolkien. A história narra a luta entre o bem e o mal na Terra Média.",
            loanPeriod = "15 dias",
            availableQuantity = 3,
            waitingListCount = 0
        )

        _book.value = when (bookId) {
            "2" -> mockBook.copy(
                id = "2",
                title = "1984",
                author = "George Orwell",
                status = BookStatus.BORROWED,
                availableQuantity = 0,
                waitingListCount = 5
            )
            "3" -> mockBook.copy(
                id = "3",
                title = "Dom Casmurro",
                author = "Machado de Assis",
                status = BookStatus.RESERVED,
                availableQuantity = 0,
                waitingListCount = 2
            )
            else -> mockBook
        }
    }
}