package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.repository.AuthRepository
import com.bibliotecadigital.app.entity.User
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CadastroResult {
    object Idle : CadastroResult()
    object Loading : CadastroResult()
    object Success : CadastroResult()
    data class Error(val message: String) : CadastroResult()
    object EmailDuplicado : CadastroResult()
}

class CadastroViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val authRepository = AuthRepository()
    private val _cadastroResult = MutableStateFlow<CadastroResult>(CadastroResult.Idle)
    val cadastroResult: StateFlow<CadastroResult> = _cadastroResult


    fun cadastrar(nome: String, email: String, senha: String) {
        if (senha.length < 8 || !senha.any { it.isDigit() } || !senha.any { it.isUpperCase() }) {
            _cadastroResult.value = CadastroResult.Error("A senha precisa ser um pouquinho mais forte: 8 caracteres, com um número e uma letra maiúscula! 💪")
            return
        }

        viewModelScope.launch {
            _cadastroResult.value = CadastroResult.Loading
            
            val user = User(
                uid = "", // Gerado pelo Firebase
                name = nome,
                email = email,
                role = "student",
                course = "",
                matricula = ""
            )
            //val userResult = FirebaseAuth.getInstance()

            val result = authRepository.signUp(user, senha)

            result.onSuccess {
                _cadastroResult.value = CadastroResult.Success
            }.onFailure { exception ->
                val errorMessage = if (exception is FirebaseAuthException) {
                    when (exception.errorCode) {
                        "ERROR_EMAIL_ALREADY_IN_USE" -> {
                            _cadastroResult.value = CadastroResult.EmailDuplicado
                            return@onFailure
                        }
                        "ERROR_WEAK_PASSWORD" -> "Essa senha está muito fraquinha. Que tal algo mais seguro? 🛡️"
                        "ERROR_INVALID_EMAIL" -> "Hum, esse e-mail não parece certo. Pode conferir o formato? 📧"
                        "ERROR_NETWORK_REQUEST_FAILED" -> "Parece que o sinal fugiu... Verifique sua internet! 📶"
                        else -> "Opa! Algo não deu certo no cadastro: ${exception.message} 😵"
                    }
                } else {
                    exception.message ?: "Tivemos um probleminha técnico. Tente de novo em instantes! 🛠️"
                }
                _cadastroResult.value = CadastroResult.Error(errorMessage)
            }
        }
    }

    fun resetResult() {
        _cadastroResult.value = CadastroResult.Idle
    }
}