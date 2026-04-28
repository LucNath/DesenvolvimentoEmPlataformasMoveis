package com.bibliotecadigital.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadigital.app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("is_logged_in", true)
                
                // Simulação simples: se o email contém "admin", loga como admin
                if (email.contains("admin")) {
                    editor.putString("user_role", "admin")
                } else {
                    editor.putString("user_role", "user")
                }
                editor.apply()

                val intent = if (email.contains("admin")) {
                    Intent(this, AdminDashboardActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            // Ação para abrir tela de cadastro (se existir)
            Toast.makeText(this, "Navegar para Cadastro", Toast.LENGTH_SHORT).show()
        }
    }
}