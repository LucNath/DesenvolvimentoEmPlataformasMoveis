package com.bibliotecadigital.app.entity

data class Loan(
    val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val title: String = "",
    val author: String = "",
    val dueDate: String = "",
    val isUrgent: Boolean = false,
    val coverUrl: String = "",
    val status: String = ""
)