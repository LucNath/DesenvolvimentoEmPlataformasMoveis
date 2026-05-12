package com.bibliotecadigital.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    data class Success(val uid: String, val role: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object ResetEmailSent : LoginResult()
}

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val db = FirebaseFirestore.getInstance()
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            
            val result = authRepository.signIn(email, senha)
            
            result.onSuccess { uid ->
                try {
                    val userDoc = db.collection("users").document(uid).get().await()
                    val role = userDoc.getString("role") ?: "user"
                    _loginResult.value = LoginResult.Success(uid, role)
                } catch (e: Exception) {
                    // Fallback se não conseguir ler do Firestore
                    val role = if (email.contains("admin")) "admin" else "user"
                    _loginResult.value = LoginResult.Success(uid, role)
                }
            }.onFailure { exception ->
                val errorMessage = when {
                    exception.message?.contains("password") == true || 
                    exception.message?.contains("wrong-password") == true -> "Senha incorreta (RF01.3)"
                    exception.message?.contains("user-not-found") == true || 
                    exception.message?.contains("no user record") == true -> "Usuário não encontrado (RF01.3)"
                    exception.message?.contains("invalid-email") == true -> "E-mail inválido"
                    exception.message?.contains("network-request-failed") == true -> "Sem conexão com a internet"
                    else -> "Falha na autenticação (RF01.3)"
                }
                _loginResult.value = LoginResult.Error(errorMessage)
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            val result = authRepository.sendPasswordResetEmail(email)
            result.onSuccess {
                _loginResult.value = LoginResult.ResetEmailSent
            }.onFailure { exception ->
                val errorMessage = when {
                    exception.message?.contains("user-not-found") == true -> "E-mail não cadastrado"
                    else -> "Erro ao enviar e-mail de recuperação"
                }
                _loginResult.value = LoginResult.Error(errorMessage)
            }
        }
    }
}