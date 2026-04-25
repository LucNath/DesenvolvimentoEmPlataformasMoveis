package com.bibliotecadigital.app

data class Fine(
    val id: String,
    val bookTitle: String,
    val daysLate: Int,
    val amount: Double,
    val status: FineStatus
)

enum class FineStatus {
    PENDENTE,
    PAGO
}