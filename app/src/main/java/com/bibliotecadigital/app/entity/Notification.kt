package com.bibliotecadigital.app.entity

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "SYSTEM_ALERT",
    val timestamp: Long = 0L,
    @field:JvmField
    var isRead: Boolean = false
)
