package com.bibliotecadigital.app

import android.os.Bundle
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

            viewModel.cadastrar(nome, email, matricula, senha)
        }

        binding.btnLogin.setOnClickListener {
            finish()
        }

        // TASK-34: Limpar erro ao digitar
        binding.etEmail.addTextChangedListener {
            binding.tilEmail.error = null
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cadastroResult.collectLatest { result ->
                when (result) {
                    is CadastroResult.EmailDuplicado -> {
                        // TASK-34: Exibir erro de e-mail duplicado
                        binding.tilEmail.error = getString(R.string.error_email_exists)
                    }
                    is CadastroResult.Success -> {
                        finish()
                    }
                    is CadastroResult.Error -> {
                        // Tratar outros erros
                    }
                    else -> {}
                }
            }
        }
    }
}