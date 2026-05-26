package com.bibliotecadigital.app.entity

data class ReadingHistoryEntry(
    val id: String,
    val title: String,
    val author: String,
    val returnDate: String,
    val coverRes: Int
)