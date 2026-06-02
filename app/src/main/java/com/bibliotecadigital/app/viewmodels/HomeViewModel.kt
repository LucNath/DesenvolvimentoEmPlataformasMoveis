package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.entity.Loan
import com.bibliotecadigital.app.entity.Reservation
import com.bibliotecadigital.app.repository.ReservationRepository
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
    val isAdmin: Boolean = false,
    val loans: List<Loan> = emptyList(),
    val reservations: List<Reservation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val reservationRepository = ReservationRepository(db)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        val uid = auth.currentUser?.uid ?: return

        _uiState.value = _uiState.value.copy(isLoading = true)

        // Observa dados do usuário (Nome e Role) em tempo real
        viewModelScope.launch {
            db.collection("users").document(uid).addSnapshotListener { snapshot, error ->
                if (snapshot != null && snapshot.exists()) {
                    val name = snapshot.getString("name") ?: "Usuário"
                    val role = snapshot.getString("role") ?: "student"
                    _uiState.value = _uiState.value.copy(
                        userName = name,
                        isAdmin = role == "admin"
                    )
                }
            }
        }

        // Observa empréstimos em tempo real
        viewModelScope.launch {
            db.collection("loans")
                .whereEqualTo("userId", uid)
                .whereEqualTo("status", "active")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        _uiState.value = _uiState.value.copy(
                            error = "Erro ao carregar empréstimos: ${error.message}",
                            isLoading = false
                        )
                        return@addSnapshotListener
                    }

                    val loans = snapshot?.toObjects(Loan::class.java)?.sortedBy {
                        try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.dueDate)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()

                    _uiState.value = _uiState.value.copy(
                        loans = loans,
                        isLoading = false
                    )
                }
        }

        // Observa reservas em tempo real separadamente
        viewModelScope.launch {
            reservationRepository.getReservations(uid).collect { reservations ->
                _uiState.value = _uiState.value.copy(reservations = reservations)
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

                // RF05.2: A renovação só é permitida se o livro não estiver reservado
                val reservationsSnapshot = db.collection("reservations")
                    .whereEqualTo("bookId", loan.bookId)
                    .whereEqualTo("status", "active")
                    .get().await()

                if (!reservationsSnapshot.isEmpty) {
                    _uiState.value = _uiState.value.copy(
                        error = "Não é possível renovar: livro possui reservas ativas.",
                        isLoading = false
                    )
                    return@launch
                }

                if (loan.renewalCount >= 3) {
                    _uiState.value = _uiState.value.copy(
                        error = "Limite de renovações atingido (máx. 3)",
                        isLoading = false
                    )
                    return@launch
                }

                db.collection("loans").document(loan.id)
                    .update(
                        "dueDate", newDueDate,
                        "renewalCount", loan.renewalCount + 1
                    )
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

    fun seedHomeData() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 1. Buscar alguns livros existentes para criar empréstimos
                val booksSnapshot = db.collection("books").limit(5).get().await()
                val books = booksSnapshot.documents
                if (books.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        error = "Popule o acervo primeiro!",
                        isLoading = false
                    )
                    return@launch
                }

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()

                // 2. Criar 2 Empréstimos
                val loan1 = Loan(
                    id = db.collection("loans").document().id,
                    userId = uid,
                    bookId = books[0].id,
                    title = books[0].getString("title") ?: "",
                    author = books[0].getString("author") ?: "",
                    coverUrl = books[0].getString("coverUrl") ?: "",
                    dueDate = sdf.format(calendar.apply { add(Calendar.DAY_OF_YEAR, 10) }.time),
                    status = "active",
                    isUrgent = false
                )
                
                calendar.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
                val loan2 = Loan(
                    id = db.collection("loans").document().id,
                    userId = uid,
                    bookId = books[1].id,
                    title = books[1].getString("title") ?: "",
                    author = books[1].getString("author") ?: "",
                    coverUrl = books[1].getString("coverUrl") ?: "",
                    dueDate = sdf.format(calendar.apply { add(Calendar.DAY_OF_YEAR, 2) }.time),
                    status = "active",
                    isUrgent = true
                )

                db.collection("loans").document(loan1.id).set(loan1)
                db.collection("loans").document(loan2.id).set(loan2)

                // 3. Criar 1 Reserva
                if (books.size > 2) {
                    val resId = db.collection("reservations").document().id
                    val reservation = Reservation(
                        id = resId,
                        userId = uid,
                        bookId = books[2].id,
                        title = books[2].getString("title") ?: "",
                        author = books[2].getString("author") ?: "",
                        coverUrl = books[2].getString("coverUrl") ?: "",
                        status = "active",
                        queuePosition = 1
                    )
                    db.collection("reservations").document(resId).set(reservation)
                }

                // 4. Criar uma Notificação
                val notifId = db.collection("users").document(uid).collection("notifications").document().id
                val notification = mapOf(
                    "id" to notifId,
                    "title" to "Bem-vindo à Biblioteca!",
                    "message" to "Agora você pode explorar nosso acervo completo e realizar empréstimos digitais.",
                    "type" to "info",
                    "timestamp" to System.currentTimeMillis(),
                    "isRead" to false
                )
                db.collection("users").document(uid).collection("notifications").document(notifId).set(notification)

                _uiState.value = _uiState.value.copy(error = "Dados de teste gerados com sucesso!", isLoading = false)
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Erro ao gerar dados: ${e.message}", isLoading = false)
            }
        }
    }
}
