package com.bibliotecadigital.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            try {
                val snapshot = db.collection("users").document(uid).get().await()
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    _uiState.value = ProfileState.Success(user)
                } else {
                    _uiState.value = ProfileState.Error("Usuário não encontrado")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.message ?: "Erro ao carregar perfil")
            }
        }
    }

    fun updateProfile(name: String, course: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(uid).update(
                    mapOf(
                        "name" to name,
                        "course" to course
                    )
                ).await()
                loadUserProfile()
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.message ?: "Erro ao atualizar perfil")
            }
        }
    }
}