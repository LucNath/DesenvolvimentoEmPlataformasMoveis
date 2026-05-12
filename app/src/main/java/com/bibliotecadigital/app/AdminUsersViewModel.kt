package com.bibliotecadigital.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AdminUsersState {
    object Loading : AdminUsersState()
    data class Success(val users: List<User>) : AdminUsersState()
    data class Error(val message: String) : AdminUsersState()
}

class AdminUsersViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow<AdminUsersState>(AdminUsersState.Loading)
    val uiState: StateFlow<AdminUsersState> = _uiState

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = AdminUsersState.Loading
            try {
                val snapshot = db.collection("users").get().await()
                val users = snapshot.toObjects(User::class.java)
                _uiState.value = AdminUsersState.Success(users)
            } catch (e: Exception) {
                _uiState.value = AdminUsersState.Error(e.message ?: "Erro ao carregar usuários")
            }
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            try {
                db.collection("users").document(user.uid).set(user).await()
                fetchUsers()
            } catch (e: Exception) {
                _uiState.value = AdminUsersState.Error(e.message ?: "Erro ao salvar usuário")
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                db.collection("users").document(userId).delete().await()
                fetchUsers()
            } catch (e: Exception) {
                _uiState.value = AdminUsersState.Error(e.message ?: "Erro ao excluir usuário")
            }
        }
    }
}
