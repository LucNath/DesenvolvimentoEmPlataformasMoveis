package com.bibliotecadigital.app

data class Loan(
    val id: String,
    val title: String,
    val author: String,
    val dueDate: String,
    val isUrgent: Boolean,
    val coverRes: Int
)