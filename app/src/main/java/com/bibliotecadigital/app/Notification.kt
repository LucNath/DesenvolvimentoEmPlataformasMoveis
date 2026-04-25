package com.bibliotecadigital.app

data class Notification(
    val id: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    var isRead: Boolean = false
)

enum class NotificationType {
    LOAN_REMINDER,    // Lembrete de devolução
    RESERVATION_READY, // Livro reservado disponível
    SYSTEM_ALERT      // Avisos gerais
}