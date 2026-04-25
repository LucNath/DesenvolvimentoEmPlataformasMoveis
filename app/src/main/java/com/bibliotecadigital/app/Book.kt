package com.bibliotecadigital.app

enum class BookStatus {
    AVAILABLE, BORROWED, RESERVED
}

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val status: BookStatus,
    val coverRes: Int,
    val isbn: String = "",
    val isMostBorrowed: Boolean = false
)