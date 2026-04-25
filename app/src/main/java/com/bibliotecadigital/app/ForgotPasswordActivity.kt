package com.bibliotecadigital.app

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadigital.app.databinding.ActivityForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    
    // Mock de e-mails cadastrados
    private val registeredEmails = listOf(
        "aluno@unipar.br",
        "professor@unipar.br",
        "admin@biblioteca.com",
        "usuario@teste.com"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnReset.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (validateEmail(email)) {
                processPasswordRecovery(email)
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        binding.tilEmail.error = null

        if (email.isEmpty()) {
            binding.tilEmail.error = "Por favor, insira seu e-mail"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Formato de e-mail inválido"
            return false
        }

        return true
    }

    private fun processPasswordRecovery(email: String) {
        if (registeredEmails.contains(email.lowercase())) {
            showSuccessMessage()
        } else {
            showErrorMessage()
        }
    }

    private fun showSuccessMessage() {
        Snackbar.make(
            binding.root,
            "Link de recuperação enviado! Verifique sua caixa de entrada.",
            Snackbar.LENGTH_LONG
        ).show()
        
        // Opcional: Desabilitar o botão após o envio para evitar múltiplos cliques
        binding.btnReset.isEnabled = false
    }

    private fun showErrorMessage() {
        Snackbar.make(
            binding.root,
            "E-mail não encontrado em nossa base de dados.",
            Snackbar.LENGTH_LONG
        ).setBackgroundTint(getColor(R.color.text_red))
        .show()
    }
}