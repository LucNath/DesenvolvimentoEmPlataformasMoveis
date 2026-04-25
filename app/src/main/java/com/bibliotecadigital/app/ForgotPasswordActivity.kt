package com.bibliotecadigital.app

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadigital.app.databinding.ActivityForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    // Lista mockada de e-mails existentes no sistema
    private val registeredEmails = listOf(
        "lucas.mendes@instituicao.edu.br",
        "aluno@biblioteca.com",
        "dev@web.com"
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

        binding.btnResetPassword.setOnClickListener {
            handleResetPassword()
        }
    }

    private fun handleResetPassword() {
        val email = binding.etEmail.text.toString().trim()

        // Reseta erro anterior
        binding.tilEmail.error = null

        if (email.isEmpty()) {
            binding.tilEmail.error = "Por favor, insira seu e-mail"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Formato de e-mail inválido"
            return
        }

        // Simulação de verificação e envio
        if (registeredEmails.contains(email)) {
            showSuccessFeedback()
        } else {
            showErrorFeedback()
        }
    }

    private fun showSuccessFeedback() {
        Snackbar.make(
            binding.root,
            "Link de redefinição enviado para seu e-mail!",
            Snackbar.LENGTH_LONG
        ).show()
        
        // Opcional: fechar a tela após alguns segundos ou deixar o usuário voltar manual
    }

    private fun showErrorFeedback() {
        binding.tilEmail.error = "E-mail não encontrado em nossa base"
        Snackbar.make(
            binding.root,
            "Erro: Verifique os dados inseridos.",
            Snackbar.LENGTH_LONG
        ).setBackgroundTint(resources.getColor(android.R.color.holo_red_dark, null))
        .show()
    }
}