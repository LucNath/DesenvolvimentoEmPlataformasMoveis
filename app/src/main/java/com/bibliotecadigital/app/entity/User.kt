package com.bibliotecadigital.app.entity

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val matricula: String = "",
    val course: String = "",
    val role: String = "student",
    val fcmToken: String = "",
    val photoUrl: String = "",
    val createdAt: Timestamp = Timestamp.Companion.now()
)