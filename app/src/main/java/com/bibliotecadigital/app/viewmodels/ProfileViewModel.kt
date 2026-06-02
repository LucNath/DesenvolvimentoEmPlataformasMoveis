package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.repository.UserRepository
import com.bibliotecadigital.app.entity.User
import com.bibliotecadigital.app.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(
        val user: User,
        val borrowedCount: Int = 0,
        val returnedCount: Int = 0,
        val reservedCount: Int = 0
    ) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val uid = authRepository.getCurrentUserUid()
        android.util.Log.d("ProfileViewModel", "Loading profile for UID: $uid")
        if (uid == null) {
            _uiState.value = ProfileState.Error("Usuário não autenticado")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            
            db.collection("users").document(uid).addSnapshotListener { userSnapshot, userError ->
                if (userError != null) {
                    android.util.Log.e("ProfileViewModel", "Error loading user: ${userError.message}")
                    _uiState.value = ProfileState.Error(userError.message ?: "Erro ao carregar perfil")
                    return@addSnapshotListener
                }

                if (userSnapshot != null && userSnapshot.exists()) {
                    android.util.Log.d("ProfileViewModel", "User document found: ${userSnapshot.data}")
                    val user = userSnapshot.toObject(User::class.java)
                    
                    if (user != null) {
                        // Busca contagens em tempo real
                        viewModelScope.launch {
                            // Observa empréstimos
                            db.collection("loans")
                                .whereEqualTo("userId", uid)
                                .addSnapshotListener { loansSnapshot, _ ->
                                    val loans = loansSnapshot?.documents ?: emptyList()
                                    val borrowed = loans.count { it.getString("status") == "active" }
                                    val returned = loans.count { it.getString("status") == "returned" }

                                    android.util.Log.d("ProfileViewModel", "Loans updated: B=$borrowed, R=$returned")

                                    // Observa reservas
                                    db.collection("reservations")
                                        .whereEqualTo("userId", uid)
                                        .whereEqualTo("status", "active")
                                        .addSnapshotListener { resSnapshot, _ ->
                                            val reserved = resSnapshot?.size() ?: 0
                                            android.util.Log.d("ProfileViewModel", "Reservations updated: $reserved")

                                            _uiState.value = ProfileState.Success(
                                                user = user,
                                                borrowedCount = borrowed,
                                                returnedCount = returned,
                                                reservedCount = reserved
                                            )
                                        }
                                }
                        }
                    } else {
                        android.util.Log.e("ProfileViewModel", "Failed to parse User object")
                        _uiState.value = ProfileState.Error("Dados do usuário corrompidos")
                    }
                } else {
                    android.util.Log.w("ProfileViewModel", "User document does NOT exist for UID: $uid. Creating one...")
                    val firebaseUser = authRepository.getCurrentUser()
                    if (firebaseUser != null) {
                        val newUser = User(
                            uid = uid,
                            name = firebaseUser.displayName ?: "Usuário",
                            email = firebaseUser.email ?: ""
                        )
                        db.collection("users").document(uid).set(newUser)
                        // O listener acima será disparado novamente após a criação
                    } else {
                        _uiState.value = ProfileState.Error("Perfil não encontrado no Firestore")
                    }
                }
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

    suspend fun changePassword(current: String, new: String): Result<Boolean> {
        return authRepository.updatePassword(current, new)
    }
}