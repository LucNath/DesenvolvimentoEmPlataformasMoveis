package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.repository.UserRepository
import com.bibliotecadigital.app.entity.User
import com.bibliotecadigital.app.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val uid = authRepository.getCurrentUserUid() ?: return
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            userRepository.getUser(uid)
                .onSuccess { user ->
                    _uiState.value = ProfileState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = ProfileState.Error(e.message ?: "Erro ao carregar perfil")
                }
        }
    }

    fun updateProfile(name: String, course: String) {
        val uid = authRepository.getCurrentUserUid() ?: return
        viewModelScope.launch {
            val fields = mapOf(
                "name" to name,
                "course" to course
            )
            userRepository.updateUser(uid, fields)
                .onSuccess {
                    loadUserProfile()
                }
                .onFailure { e ->
                    _uiState.value = ProfileState.Error(e.message ?: "Erro ao atualizar perfil")
                }
        }
    }
}