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
}

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val db = FirebaseFirestore.getInstance()
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            
            val result = authRepository.login(email, senha)
            
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
                _loginResult.value = LoginResult.Error(exception.message ?: "Falha na autenticação")
            }
        }
    }
}