package com.bibliotecadigital.app.repository

class SupportRepository {
    fun sendFeedback(category: String, description: String, callback: (Boolean) -> Unit) {
        // Simulação de envio para o servidor
        callback(true)
    }
}