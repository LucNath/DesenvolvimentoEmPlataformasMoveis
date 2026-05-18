package com.bibliotecadigital.app

import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val bookId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val date: String = ""
)