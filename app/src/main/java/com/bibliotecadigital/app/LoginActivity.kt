package com.bibliotecadigital.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.loginWithGoogle(idToken)
                } else {
                    Toast.makeText(this, "Erro ao obter ID Token", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Erro no Google Sign-In: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignIn()
        setupListeners()
        observeViewModel()
    }

    private fun setupGoogleSignIn() {
        val webClientId = getString(R.string.default_web_client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupListeners() {
        binding.btnGoogleLogin.setOnClickListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isNotEmpty()) {
                viewModel.resetPassword(email)
            } else {
                binding.tilEmail.error = "Insira seu e-mail para recuperar a senha"
            }
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
                // Reset errors
                binding.tilEmail.error = null
                binding.tilPassword.error = null

                when (result) {
                    is LoginResult.Loading -> {
                        binding.btnLogin.isEnabled = false
                        binding.btnGoogleLogin.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is LoginResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.btnGoogleLogin.isEnabled = true
                        
                        val appPrefs = AppPrefs(this@LoginActivity)
                        appPrefs.isLoggedIn = true
                        appPrefs.userEmail = result.email
                        appPrefs.userRole = result.role

                        val intent = if (result.role == "admin") {
                            Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                        } else {
                            Intent(this@LoginActivity, MainActivity::class.java)
                        }
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    is LoginResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.btnGoogleLogin.isEnabled = true
                        
                        val msg = result.message.lowercase()
                        when {
                            msg.contains("e-mail") || msg.contains("usuário") || msg.contains("user") || msg.contains("email") -> {
                                binding.tilEmail.error = result.message
                            }
                            msg.contains("senha") || msg.contains("password") -> {
                                binding.tilPassword.error = result.message
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    is LoginResult.ResetEmailSent -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.btnGoogleLogin.isEnabled = true
                        Snackbar.make(binding.root, "E-mail de recuperação enviado!", Snackbar.LENGTH_LONG).show()
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.btnGoogleLogin.isEnabled = true
                    }
                }
            }
        }
    }
}