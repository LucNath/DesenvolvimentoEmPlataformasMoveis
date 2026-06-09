package com.bibliotecadigital.app.entity

data class Loan(
    val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val title: String = "",
    val author: String = "",
    val dueDate: String = "",
    val returnDate: String = "", // Adicionado para RF-HL02
    val isUrgent: Boolean = false,
    val coverUrl: String = "",
    val status: String = "", // "active", "returned", "overdue"
    val renewalCount: Int = 0,
    val rating: Float? = null // Adicionado para RF-HL06
)
