package com.bibliotecadigital.app

data class Event(
    val id: String,
    val name: String,
    val date: String,
    val time: String,
    val facilitator: String,
    var availableSpots: Int,
    var isRegistered: Boolean = false
)