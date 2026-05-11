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

    private val _cadastroResult = MutableStateFlow<CadastroResult>(CadastroResult.Idle)
    val cadastroResult: StateFlow<CadastroResult> = _cadastroResult

    fun cadastrar(nome: String, email: String, matricula: String, senha: String) {
        viewModelScope.launch {
            _cadastroResult.value = CadastroResult.Loading
            
            // Simulação de chamada de API
            delay(1500)

            // Regra de negócio simulada para e-mail duplicado
            if (email == "teste@duplicado.com") {
                _cadastroResult.value = CadastroResult.EmailDuplicado
            } else {
                _cadastroResult.value = CadastroResult.Success
            }
        }
    }

    fun resetResult() {
        _cadastroResult.value = CadastroResult.Idle
    }
}