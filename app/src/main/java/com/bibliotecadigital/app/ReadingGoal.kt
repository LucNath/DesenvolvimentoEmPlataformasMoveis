package com.bibliotecadigital.app

import java.util.Date

enum class GoalStatus {
    EM_ANDAMENTO, CONCLUIDA, ATRASADA
}

data class ReadingGoal(
    val id: String,
    val title: String,
    val quantity: Int,
    val deadline: String, // Usando String para simplificar mock, mas poderia ser Date
    var progress: Int = 0,
    val status: GoalStatus = GoalStatus.EM_ANDAMENTO
) {
    val progressText: String
        get() = "$progress de $quantity livros lidos"
    
    val percentage: Int
        get() = if (quantity > 0) (progress * 100) / quantity else 0
}