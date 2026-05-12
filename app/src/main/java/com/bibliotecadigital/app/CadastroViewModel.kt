package com.bibliotecadigital.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
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

    private val authRepository = AuthRepository()
    private val _cadastroResult = MutableStateFlow<CadastroResult>(CadastroResult.Idle)
    val cadastroResult: StateFlow<CadastroResult> = _cadastroResult

    fun cadastrar(nome: String, email: String, matricula: String, senha: String) {
        viewModelScope.launch {
            _cadastroResult.value = CadastroResult.Loading
            
            val user = User(
                id = "", // Gerado pelo Firebase
                name = nome,
                email = email,
                role = "user",
                registrationDate = System.currentTimeMillis().toString(),
                course = matricula // Usando campo matricula no campo course ou adaptando se necessário
            )

            val result = authRepository.signUp(user, senha)
            
            result.onSuccess {
                _cadastroResult.value = CadastroResult.Success
            }.onFailure { exception ->
                val errorMessage = when {
                    exception.message?.contains("email address is already in use") == true -> {
                        _cadastroResult.value = CadastroResult.EmailDuplicado
                        return@onFailure
                    }
                    exception.message?.contains("weak-password") == true -> "A senha deve ter pelo menos 6 caracteres (RF02.2)"
                    exception.message?.contains("invalid-email") == true -> "E-mail inválido (RF02.3)"
                    else -> "Erro ao realizar cadastro. Tente novamente."
                }
                _cadastroResult.value = CadastroResult.Error(errorMessage)
            }
        }
    }

    fun resetResult() {
        _cadastroResult.value = CadastroResult.Idle
    }
}