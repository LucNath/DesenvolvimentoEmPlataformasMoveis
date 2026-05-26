package com.bibliotecadigital.app.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Book(
    @DocumentId val id: String = "",
    val title: String = "",
    val author: String = "",
    val category: String = "",
    val coverUrl: String = "",
    val available: Int = 0,
    val total: Int = 0,
    val status: String = "available",
    val isbn: String = "",
    val synopsis: String = "",
    val publisher: String = "",
    val year: String = "",
    val isMostBorrowed: Boolean = false,
    val rating: Float = 0f,
    val createdAt: Timestamp = Timestamp.Companion.now()
)