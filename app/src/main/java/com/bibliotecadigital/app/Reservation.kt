// Apague o que tiver e coloque:
package com.bibliotecadigital.app

data class Reservation(
    val id: String,
    val title: String,
    val author: String,
    val coverRes: Int,
    val queuePosition: Int
)