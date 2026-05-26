package com.bibliotecadigital.app.entity

data class Notification(
    val id: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.SYSTEM_ALERT,
    val timestamp: String = "",
    @field:JvmField
    var isRead: Boolean = false
)

enum class NotificationType {
    LOAN_REMINDER,    // Lembrete de devolução
    RESERVATION_READY, // Livro reservado disponível
    SYSTEM_ALERT      // Avisos gerais
}
