package com.bibliotecadigital.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.databinding.ActivityLoginBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
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
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginResult.collectLatest { result ->
                when (result) {
                    is LoginResult.Loading -> {
                        binding.btnLogin.isEnabled = false
                        // Adicionar ProgressBar se houver
                    }
                    is LoginResult.Success -> {
                        binding.btnLogin.isEnabled = true
                        val appPrefs = AppPrefs(this@LoginActivity)
                        appPrefs.isLoggedIn = true
                        appPrefs.userEmail = binding.etEmail.text.toString()
                        appPrefs.userRole = result.role

                        val intent = if (result.role == "admin") {
                            Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                        } else {
                            Intent(this@LoginActivity, MainActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    }
                    is LoginResult.Error -> {
                        binding.btnLogin.isEnabled = true
                        Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        binding.btnLogin.isEnabled = true
                    }
                }
            }
        }
    }
}