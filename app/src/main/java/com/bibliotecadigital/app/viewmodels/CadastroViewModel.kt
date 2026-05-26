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


    fun cadastrar(nome: String, email: String, matricula: String, senha: String) {

        viewModelScope.launch {
            _cadastroResult.value = CadastroResult.Loading
            
            val user = User(
                uid = "", // Gerado pelo Firebase
                name = nome,
                email = email,
                role = "admin",
                course = "", // Adicionado depois ou via matricula se for o caso
                matricula = matricula
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
                        "ERROR_WEAK_PASSWORD" -> "A senha deve ter pelo menos 6 caracteres (RF02.2)"
                        "ERROR_INVALID_EMAIL" -> "E-mail inválido (RF02.3)"
                        "ERROR_NETWORK_REQUEST_FAILED" -> "Sem conexão com a internet"
                        else -> "Erro na autenticação: ${exception.message}"
                    }
                } else {
                    exception.message ?: "Erro ao realizar cadastro. Tente novamente."
                }
                _cadastroResult.value = CadastroResult.Error(errorMessage)
            }
        }
    }

    fun resetResult() {
        _cadastroResult.value = CadastroResult.Idle
    }
}