package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.databinding.ActivityForgotPasswordBinding
import com.bibliotecadigital.app.viewmodels.LoginResult
import com.bibliotecadigital.app.viewmodels.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnReset.setOnClickListener {
            val email = binding.etEmailReset.text.toString()
            if (email.isNotEmpty()) {
                viewModel.resetPassword(email)
            } else {
                Toast.makeText(this, "Por favor, insira seu e-mail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginResult.collectLatest { result ->
                when (result) {
                    is LoginResult.Loading -> {
                        binding.btnReset.isEnabled = false
                    }
                    is LoginResult.ResetEmailSent -> {
                        binding.btnReset.isEnabled = true
                        Toast.makeText(this@ForgotPasswordActivity,
                            "E-mail de recuperação enviado!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    is LoginResult.Error -> {
                        binding.btnReset.isEnabled = true
                        Toast.makeText(this@ForgotPasswordActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }
}