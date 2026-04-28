package com.bibliotecadigital.app

data class ReadingHistoryEntry(
    val id: String,
    val title: String,
    val author: String,
    val returnDate: String,
    val coverRes: Int
)