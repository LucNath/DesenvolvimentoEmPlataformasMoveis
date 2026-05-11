package com.bibliotecadigital.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HomeUiState(
    val userName: String = "",
    val loans: List<Loan> = emptyList(),
    val reservations: List<Reservation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        val uid = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Carregar nome do usuário
                val userSnapshot = db.collection("users").document(uid).get().await()
                val name = userSnapshot.getString("name") ?: "Usuário"

                // Carregar empréstimos
                val loansSnapshot = db.collection("loans")
                    .whereEqualTo("userId", uid)
                    .get().await()
                val loans = loansSnapshot.toObjects(Loan::class.java)

                // Carregar reservas
                val reservationsSnapshot = db.collection("reservations")
                    .whereEqualTo("userId", uid)
                    .get().await()
                val reservations = reservationsSnapshot.toObjects(Reservation::class.java)

                _uiState.value = _uiState.value.copy(
                    userName = name,
                    loans = loans,
                    reservations = reservations,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Erro ao carregar dados",
                    isLoading = false
                )
            }
        }
    }

    fun renewLoan(loan: Loan) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDueDate = sdf.parse(loan.dueDate) ?: Calendar.getInstance().time
                
                val calendar = Calendar.getInstance()
                calendar.time = currentDueDate
                calendar.add(Calendar.DAY_OF_YEAR, 15)
                val newDueDate = sdf.format(calendar.time)

                db.collection("loans").document(loan.id)
                    .update("dueDate", newDueDate)
                    .await()

                // Recarrega os dados após a renovação
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Erro ao renovar empréstimo",
                    isLoading = false
                )
            }
        }
    }
}