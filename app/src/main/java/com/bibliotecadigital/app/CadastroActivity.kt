package com.bibliotecadigital.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.databinding.ActivityCadastroBinding
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
            val nome = binding.etNome.text.toString()
            val email = binding.etEmail.text.toString()
            val matricula = binding.etMatricula.text.toString()
            val senha = binding.etSenha.text.toString()

            if (nome.isNotEmpty() && email.isNotEmpty() && matricula.isNotEmpty() && senha.isNotEmpty()) {
                viewModel.cadastrar(nome, email, matricula, senha)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            finish()
        }

        // Limpar erros ao digitar
        binding.etEmail.addTextChangedListener {
            binding.tilEmail.error = null
        }
        binding.etSenha.addTextChangedListener {
            binding.tilSenha.error = null
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cadastroResult.collectLatest { result ->
                when (result) {
                    is CadastroResult.Loading -> {
                        binding.btnCriarConta.isEnabled = false
                        binding.progressBar.visibility = android.view.View.VISIBLE
                    }
                    is CadastroResult.EmailDuplicado -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        binding.btnCriarConta.isEnabled = true
                        binding.tilEmail.error = getString(R.string.error_email_exists)
                    }
                    is CadastroResult.Success -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@CadastroActivity, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is CadastroResult.Error -> {
                        binding.progressBar.visibility = android.view.View.GONE
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
                        binding.progressBar.visibility = android.view.View.GONE
                        binding.btnCriarConta.isEnabled = true
                    }
                }
            }
        }
    }
}