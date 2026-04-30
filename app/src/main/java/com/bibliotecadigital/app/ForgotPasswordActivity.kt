package com.bibliotecadigital.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadigital.app.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")

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
            resetErrors()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (validatePassword(newPassword) && validateConfirmation(newPassword, confirmPassword)) {
                // Lógica de sucesso
                Toast.makeText(this, "Senha redefinida com sucesso!", Toast.LENGTH_SHORT).show()
                // Aqui entraria a chamada ao backend futuramente
            }
        }
    }

    private fun resetErrors() {
        binding.tilNewPassword.error = null
        binding.tilConfirmPassword.error = null
    }

    private fun validatePassword(password: String): Boolean {
        return if (!passwordRegex.matches(password)) {
            binding.tilNewPassword.error = "A senha deve ter pelo menos 8 caracteres, incluindo um número e uma letra maiúscula"
            false
        } else {
            true
        }
    }

    private fun validateConfirmation(password: String, confirmation: String): Boolean {
        return if (password != confirmation) {
            binding.tilConfirmPassword.error = "As senhas não coincidem"
            false
        } else {
            true
        }
    }
}