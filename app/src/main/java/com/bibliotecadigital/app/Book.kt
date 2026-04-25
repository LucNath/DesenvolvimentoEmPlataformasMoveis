package com.bibliotecadigital.app

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val available: Boolean,
    val coverRes: Int
)