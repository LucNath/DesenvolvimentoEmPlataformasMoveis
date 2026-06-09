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
            binding.tilNome.error = "Como podemos te chamar? Informe seu nome ✨"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Precisamos do seu e-mail institucional para o cadastro 📧"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Ops! Esse e-mail não parece estar certinho. Pode conferir? 🧐"
            isValid = false
        }

        if (senha.isEmpty()) {
            binding.tilSenha.error = "Não esqueça de criar uma senha segura! 🔒"
            isValid = false
        } else if (senha.length < 8) {
            binding.tilSenha.error = "A senha precisa ter pelo menos 8 caracteres para sua segurança 🛡️"
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
                        val msg = result.message.lowercase()
                        if (msg.contains("senha") || msg.contains("rf02.2")) {
                            binding.tilSenha.error = result.message
                        } else if (msg.contains("e-mail") || msg.contains("email") || msg.contains("rf02.3")) {
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