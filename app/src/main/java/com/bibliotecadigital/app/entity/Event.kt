package com.bibliotecadigital.app.entity

import com.google.firebase.firestore.DocumentId

data class Event(
    @DocumentId val id: String = "",
    val name: String = "",
    val date: String = "",
    val time: String = "",
    val facilitator: String = "",
    val totalSlots: Int = 0,
    val usedSlots: Int = 0,
    val participants: List<String> = emptyList()
)