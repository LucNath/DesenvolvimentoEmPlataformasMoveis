package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.entity.Loan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReadingHistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _historyItems = MutableStateFlow<List<Loan>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message

    val filteredHistory = combine(_historyItems, _searchQuery) { items, query ->
        if (query.isEmpty()) {
            items
        } else {
            items.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.author.contains(query, ignoreCase = true)
            }
        }
    }

    fun loadHistory() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("loans")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("status", "returned")
                    .orderBy("returnDate", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Loan::class.java)?.copy(id = doc.id)
                }
                _historyItems.value = items
            } catch (e: Exception) {
                _message.emit("Erro ao carregar histórico: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateRating(loanId: String, rating: Float) {
        viewModelScope.launch {
            try {
                db.collection("loans").document(loanId)
                    .update("rating", rating)
                    .await()
                
                // Atualiza a lista localmente para refletir a mudança
                _historyItems.value = _historyItems.value.map {
                    if (it.id == loanId) it.copy(rating = rating) else it
                }
                _message.emit("Avaliação registrada!")
            } catch (e: Exception) {
                _message.emit("Erro ao salvar avaliação: ${e.message}")
            }
        }
    }
}
