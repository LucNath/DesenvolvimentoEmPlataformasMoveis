package com.bibliotecadigital.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.ui.MainActivity
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.ActivityCadastroBinding
import com.bibliotecadigital.app.viewmodels.CadastroResult
import com.bibliotecadigital.app.viewmodels.CadastroViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private val viewModel: CadastroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnCriarConta.setOnClickListener {
            val nome = binding.etNome.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val senha = binding.etSenha.text.toString()

            if (validateFields(nome, email, senha)) {
                viewModel.cadastrar(nome, email, senha)
            }
        }

        binding.btnLogin.setOnClickListener {
            finish()
        }

        // Limpar erros ao digitar
        binding.etNome.addTextChangedListener { binding.tilNome.error = null }
        binding.etEmail.addTextChangedListener { binding.tilEmail.error = null }
        binding.etSenha.addTextChangedListener { binding.tilSenha.error = null }
    }

    private fun validateFields(nome: String, email: String, senha: String): Boolean {
        var isValid = true

        if (nome.isEmpty()) {
            binding.tilNome.error = "Informe seu nome completo"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Informe seu e-mail institucional"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "E-mail inválido"
            isValid = false
        }

        if (senha.isEmpty()) {
            binding.tilSenha.error = "Informe uma senha"
            isValid = false
        } else if (senha.length < 8) {
            binding.tilSenha.error = "Mínimo de 8 caracteres"
            isValid = false
        }

        return isValid
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cadastroResult.collectLatest { result ->
                when (result) {
                    is CadastroResult.Loading -> {
                        binding.btnCriarConta.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is CadastroResult.EmailDuplicado -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnCriarConta.isEnabled = true
                        binding.tilEmail.error = getString(R.string.error_email_exists)
                    }
                    is CadastroResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@CadastroActivity, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                        val mainIntent = Intent(this@CadastroActivity, MainActivity::class.java)
                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainIntent)
                        finish()
                    }
                    is CadastroResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnCriarConta.isEnabled = true
                        if (result.message.contains("senha") || result.message.contains("RF02.2")) {
                            binding.tilSenha.error = result.message
                        } else if (result.message.contains("E-mail") || result.message.contains("RF02.3")) {
                            binding.tilEmail.error = result.message
                        } else {
                            Toast.makeText(this@CadastroActivity, result.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnCriarConta.isEnabled = true
                    }
                }
            }
        }
    }
}