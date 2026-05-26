package com.bibliotecadigital.app.entity

import com.google.firebase.firestore.DocumentId

data class Reservation(
    @DocumentId val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val title: String = "",
    val author: String = "",
    val coverUrl: String = "",
    val queuePosition: Int = 0,
    val status: String = "active"
)