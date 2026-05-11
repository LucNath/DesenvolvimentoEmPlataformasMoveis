package com.bibliotecadigital.app

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "user", // "admin" or "user"
    val registrationDate: String = "",
    val course: String = ""
)