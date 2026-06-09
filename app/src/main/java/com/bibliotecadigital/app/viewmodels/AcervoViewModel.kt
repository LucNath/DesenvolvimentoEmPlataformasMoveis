package com.bibliotecadigital.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.entity.Book
import com.bibliotecadigital.app.repository.BookRepository
import com.bibliotecadigital.app.repository.LoanRepository
import com.bibliotecadigital.app.repository.ReservationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class AcervoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val bookRepository = BookRepository(db)
    private val loanRepository = LoanRepository(db)
    private val reservationRepository = ReservationRepository(db)

    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _actionMessage = MutableSharedFlow<String>()
    val actionMessage: SharedFlow<String> = _actionMessage.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Pair<String, String>>()
    val navigationEvent: SharedFlow<Pair<String, String>> = _navigationEvent.asSharedFlow()

    val categories: StateFlow<List<String>> = _allBooks.map { books ->
        val list = books.asSequence().map { it.category }.distinct().filter { it.isNotEmpty() }.sorted().toMutableList()
        if (!list.contains("Todas")) list.add(0, "Todas")
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Todas"))

    val mostBorrowedBooks: StateFlow<List<Book>> = _allBooks.map { books ->
        books.filter { it.isMostBorrowed }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredBooks: StateFlow<List<Book>> = combine(
        _allBooks,
        _searchQuery,
        _selectedCategory
    ) { books, query, category ->
        books.filter { book ->
            val matchesQuery = book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true) ||
                    book.isbn.contains(query, ignoreCase = true)

            val matchesCategory = category == "Todas" || book.category == category

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observeBooks()
        observeUserLoans()
    }

    private fun observeUserLoans() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            // Monitora empréstimos E reservas para desabilitar o botão se o usuário já tiver ação no livro
            val loansFlow = callbackFlow {
                val sub = db.collection("loans")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("status", "active")
                    .addSnapshotListener { snapshot, _ ->
                        trySend(snapshot?.documents?.mapNotNull { it.getString("bookId") } ?: emptyList())
                    }
                awaitClose { sub.remove() }
            }

            val reservationsFlow = callbackFlow {
                val sub = db.collection("reservations")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("status", "active")
                    .addSnapshotListener { snapshot, _ ->
                        trySend(snapshot?.documents?.mapNotNull { it.getString("bookId") } ?: emptyList())
                    }
                awaitClose { sub.remove() }
            }

            combine(loansFlow, reservationsFlow) { loans: List<String>, reservations: List<String> ->
                loans + reservations
            }.collect { activeIds ->
                val updatedBooks = _allBooks.value.map { b ->
                    b.copy(isBorrowedByUser = activeIds.contains(b.id))
                }
                _allBooks.value = updatedBooks
            }
        }
    }

    private fun observeBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                bookRepository.getBooks().collect { books ->
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        // Ao carregar os livros, já verifica o status de empréstimo
                        val loansSnapshot = db.collection("loans")
                            .whereEqualTo("userId", userId)
                            .whereEqualTo("status", "active")
                            .get().await()
                        
                        val borrowedIds = loansSnapshot.documents.mapNotNull { doc -> doc.getString("bookId") }
                        val updatedBooks = books.map { b ->
                            b.copy(isBorrowedByUser = borrowedIds.contains(b.id))
                        }
                        _allBooks.value = updatedBooks
                    } else {
                        _allBooks.value = books
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("AcervoViewModel", "Error observing books", e)
                _allBooks.value = emptyList()
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun handleBookAction(book: Book) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (book.availableQuantity > 0) {
                    // Realizar Empréstimo
                    loanRepository.createLoan(
                        userId = userId,
                        bookId = book.id,
                        title = book.title,
                        author = book.author,
                        coverUrl = book.coverUrl
                    ).onSuccess { dueDate ->
                        _navigationEvent.emit(book.title to dueDate)
                    }.onFailure { e ->
                        _actionMessage.emit("Erro ao solicitar reserva: ${e.message}")
                    }
                } else {
                    // Realizar Reserva
                    reservationRepository.createReservation(
                        userId = userId,
                        bookId = book.id,
                        title = book.title,
                        author = book.author,
                        coverUrl = book.coverUrl
                    ).onSuccess {
                        _actionMessage.emit("Reserva realizada com sucesso!")
                    }.onFailure { e ->
                        _actionMessage.emit("Erro ao reservar: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                _actionMessage.emit("Ocorreu um erro inesperado")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Função para popular o banco de dados inicialmente (Chamar apenas uma vez)
    fun seedDatabase() {
        viewModelScope.launch {
            val initialBooks = listOf(
                Book(
                    title = "Direito Civil Brasileiro - Vol 1",
                    author = "Carlos Roberto Gonçalves",
                    category = "Direito",
                    isbn = "9788553606626",
                    availableQuantity = 5,
                    quantity = 5,
                    status = "available",
                    isMostBorrowed = true,
                    publisher = "Saraiva",
                    year = "2023",
                    synopsis = "Parte geral do Direito Civil, abordando os conceitos fundamentais, as pessoas, os bens e os fatos jurídicos de acordo com o Código Civil de 2002.",
                    rating = 4.8f,
                    coverUrl = "https://m.media-amazon.com/images/I/41D+s1E8GHL.jpg"
                ),
                Book(
                    title = "Vade Mecum 2024",
                    author = "Equipe Saraiva",
                    category = "Direito",
                    isbn = "9786553612345",
                    availableQuantity = 10,
                    quantity = 10,
                    status = "available",
                    isMostBorrowed = true,
                    publisher = "Saraiva",
                    year = "2024",
                    synopsis = "A mais completa e atualizada legislação brasileira, indispensável para estudantes e profissionais do Direito.",
                    rating = 4.9f,
                    coverUrl = "https://m.media-amazon.com/images/I/71rO16I0ZDL.jpg"
                ),
                Book(
                    title = "O Processo",
                    author = "Franz Kafka",
                    category = "Literatura",
                    isbn = "9788535902341",
                    availableQuantity = 2,
                    quantity = 3,
                    status = "available",
                    isMostBorrowed = false,
                    publisher = "Companhia das Letras",
                    year = "1925",
                    synopsis = "Josef K. acorda certa manhã e é preso sem que tenha cometido qualquer crime. Ele se vê preso em um labirinto burocrático e jurídico.",
                    rating = 4.5f,
                    coverUrl = "https://m.media-amazon.com/images/I/81S8A++G4yL.jpg"
                ),
                Book(
                    title = "Dom Casmurro",
                    author = "Machado de Assis",
                    category = "Literatura Brasileira",
                    isbn = "9788535902342",
                    availableQuantity = 3,
                    quantity = 5,
                    status = "available",
                    isMostBorrowed = true,
                    publisher = "Principis",
                    year = "1899",
                    synopsis = "Bento Santiago conta a história de sua vida e seu amor por Capitu, levantando a eterna dúvida sobre a traição.",
                    rating = 4.7f,
                    coverUrl = "https://m.media-amazon.com/images/I/71p-T4fT4VL.jpg"
                ),
                Book(
                    title = "Código Penal Comentado",
                    author = "Guilherme Nucci",
                    category = "Direito",
                    isbn = "9788530982345",
                    availableQuantity = 4,
                    quantity = 4,
                    status = "available",
                    isMostBorrowed = false,
                    publisher = "Forense",
                    year = "2023",
                    synopsis = "Estudo detalhado do Código Penal brasileiro com jurisprudência e doutrina atualizada.",
                    rating = 4.6f,
                    coverUrl = "https://m.media-amazon.com/images/I/61r590tOq1L.jpg"
                ),
                Book(
                    title = "Sapiens: Uma Breve História da Humanidade",
                    author = "Yuval Noah Harari",
                    category = "História",
                    isbn = "9788525432186",
                    availableQuantity = 6,
                    quantity = 8,
                    status = "available",
                    isMostBorrowed = true,
                    publisher = "L&PM",
                    year = "2015",
                    synopsis = "O autor percorre a história da humanidade, desde a evolução do Homo sapiens até as revoluções cognitiva, agrícola e científica.",
                    rating = 4.9f,
                    coverUrl = "https://m.media-amazon.com/images/I/716m8ZpXqSL.jpg"
                ),
                Book(
                    title = "1984",
                    author = "George Orwell",
                    category = "Literatura",
                    isbn = "9788535914849",
                    availableQuantity = 4,
                    quantity = 6,
                    status = "available",
                    isMostBorrowed = true,
                    publisher = "Companhia das Letras",
                    year = "1949",
                    synopsis = "Uma distopia clássica sobre um regime totalitário vigiado pelo Grande Irmão.",
                    rating = 4.8f,
                    coverUrl = "https://m.media-amazon.com/images/I/819js3EQwbL.jpg"
                ),
                Book(
                    title = "O Senhor dos Anéis: A Sociedade do Anel",
                    author = "J.R.R. Tolkien",
                    category = "Fantasia",
                    isbn = "9788595084742",
                    availableQuantity = 5,
                    quantity = 5,
                    status = "available",
                    isMostBorrowed = true,
                    publisher = "HarperCollins",
                    year = "1954",
                    synopsis = "Frodo Bolseiro recebe a missão de destruir o Um Anel para salvar a Terra-média das garras de Sauron.",
                    rating = 5.0f,
                    coverUrl = "https://m.media-amazon.com/images/I/81hCV7+VdcL.jpg"
                ),
                Book(
                    title = "A Menina que Roubava Livros",
                    author = "Markus Zusak",
                    category = "Ficção",
                    isbn = "9788575422434",
                    availableQuantity = 3,
                    quantity = 4,
                    status = "available",
                    isMostBorrowed = false,
                    publisher = "Intrínseca",
                    year = "2005",
                    synopsis = "Narrado pela Morte, o livro conta a história de Liesel Meminger, uma menina que encontra conforto nos livros durante a Segunda Guerra Mundial.",
                    rating = 4.7f,
                    coverUrl = "https://m.media-amazon.com/images/I/81yvM7ZkZGL.jpg"
                ),
                Book(
                    title = "Pai Rico, Pai Pobre",
                    author = "Robert Kiyosaki",
                    category = "Finanças",
                    isbn = "9788550801483",
                    availableQuantity = 8,
                    quantity = 10,
                    status = "available",
                    isMostBorrowed = true,
                    publisher = "Alta Books",
                    year = "1997",
                    synopsis = "O livro ensina lições sobre independência financeira através do investimento em ativos e educação financeira.",
                    rating = 4.6f,
                    coverUrl = "https://m.media-amazon.com/images/I/81M7r5VlYOL.jpg"
                )
            )

            initialBooks.forEach { book ->
                bookRepository.addBook(book)
            }
        }
    }
}