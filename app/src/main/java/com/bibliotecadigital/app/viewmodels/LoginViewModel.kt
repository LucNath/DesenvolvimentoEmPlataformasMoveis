package com.bibliotecadigital.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    data class Success(val uid: String, val role: String, val email: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object ResetEmailSent : LoginResult()
}

class LoginViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val authRepository = AuthRepository()
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Iniciando login para: $email")
            _loginResult.value = LoginResult.Loading
            
            val result = authRepository.signIn(email, senha)

            result.onSuccess { uid ->
                Log.d("LoginViewModel", "Login com sucesso, UID: $uid. Buscando papel do usuário...")
                fetchUserRoleAndFinish(uid, email)
            }.onFailure { exception ->
                Log.e("LoginViewModel", "Erro no login: ${exception.message}")
                handleLoginFailure(exception)
            }
        }
    }

    private suspend fun fetchUserRoleAndFinish(uid: String, email: String) {
        try {
            // Adicionamos um timeout de 5 segundos para não travar o login se o Firestore falhar
            val userDoc = withTimeoutOrNull(5000) {
                db.collection("users").document(uid).get().await()
            }
            
            val role = userDoc?.getString("role") ?: "student"
            Log.d("LoginViewModel", "Papel do usuário obtido: $role")
            _loginResult.value = LoginResult.Success(uid, role, email)
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Erro ao buscar papel, usando padrão 'student': ${e.message}")
            _loginResult.value = LoginResult.Success(uid, "student", email)
        }
    }

    private fun handleLoginFailure(exception: Throwable) {
        val errorMessage = if (exception is FirebaseAuthException) {
            when (exception.errorCode) {
                "ERROR_WRONG_PASSWORD" -> "Ops! A senha está incorreta. Tente novamente 🧐"
                "ERROR_USER_NOT_FOUND" -> "Não encontramos esse e-mail por aqui. Verificou se está certinho? ✨"
                "ERROR_INVALID_EMAIL" -> "Esse e-mail parece um pouco estranho... Pode conferir? 📧"
                "ERROR_USER_DISABLED" -> "Esta conta foi desativada. Entre em contato com o suporte 🔒"
                "ERROR_NETWORK_REQUEST_FAILED" -> "Parece que você está sem internet. Verifique sua conexão 📶"
                "INVALID_LOGIN_CREDENTIALS" -> "E-mail ou senha incorretos. Que tal conferir os dados? ✌️"
                else -> "Algo deu errado: ${exception.message} 😵"
            }
        } else {
            exception.message ?: "Opa! Tivemos um probleminha técnico. Tente mais tarde 🛠️"
        }
        _loginResult.value = LoginResult.Error(errorMessage)
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            val result = authRepository.sendPasswordResetEmail(email)
            result.onSuccess {
                _loginResult.value = LoginResult.ResetEmailSent
            }.onFailure { exception ->
                val errorMessage = if (exception is FirebaseAuthException) {
                    when (exception.errorCode) {
                        "ERROR_USER_NOT_FOUND" -> "Email não cadastrado"
                        "ERROR_INVALID_EMAIL" -> "Formato de e-mail inválido"
                        "ERROR_NETWORK_REQUEST_FAILED" -> "Sem conexão com a internet"
                        else -> "Erro ao enviar e-mail: ${exception.message}"
                    }
                } else {
                    exception.message ?: "Erro ao enviar e-mail de recuperação"
                }
                _loginResult.value = LoginResult.Error(errorMessage)
            }
        }
    }
}