package com.bibliotecadigital.app

data class Reservation(
    val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val title: String = "",
    val author: String = "",
    val coverUrl: String = "",
    val queuePosition: Int = 0
)