package com.bibliotecadigital.app

enum class BookStatus {
    AVAILABLE, BORROWED, RESERVED
}

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val category: String = "",
    val status: BookStatus = BookStatus.AVAILABLE,
    val coverUrl: String = "",
    val isbn: String = "",
    val publisher: String = "",
    val year: String = "",
    val synopsis: String = "",
    val loanPeriod: String = "15 dias",
    val availableQuantity: Int = 0,
    val waitingListCount: Int = 0,
    val isMostBorrowed: Boolean = false
)