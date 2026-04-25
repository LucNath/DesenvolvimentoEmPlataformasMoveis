package com.bibliotecadigital.app

data class Review(
    val id: String,
    val userName: String,
    val bookId: String,
    var rating: Float,
    var comment: String,
    val date: String
)