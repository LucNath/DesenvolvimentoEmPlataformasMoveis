package com.bibliotecadigital.app

data class ReadingGoal(
    val id: String,
    val quantity: Int,
    val period: String,
    val progress: Int
) {
    val progressText: String
        get() = "$progress de $quantity livros lidos"
    
    val percentage: Int
        get() = if (quantity > 0) (progress * 100) / quantity else 0
}