package com.bibliotecadigital.app.repository

import com.bibliotecadigital.app.entity.Loan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class LoanRepository(private val db: FirebaseFirestore) : FirestoreRepository(db) {

    private val loansCollection = "loans"

    fun getUserLoans(userId: String): Flow<List<Loan>> {
        return getFilteredCollection(loansCollection, Loan::class.java, "userId" to userId)
    }

    suspend fun createLoan(userId: String, bookId: String, title: String, author: String, coverUrl: String): Result<String> = runCatching {
        // RF01: Evita empréstimos duplicados do mesmo livro para o mesmo usuário
        val activeLoan = db.collection(loansCollection)
            .whereEqualTo("userId", userId)
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("status", "active")
            .get()
            .await()

        if (!activeLoan.isEmpty) {
            throw Exception("Você já possui um empréstimo ativo deste livro.")
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        // Data atual como início e 15 dias para devolução
        calendar.add(Calendar.DAY_OF_YEAR, 15)
        val dueDate = sdf.format(calendar.time)

        val docRef = db.collection(loansCollection).document()
        val loan = Loan(
            id = docRef.id,
            userId = userId,
            bookId = bookId,
            title = title,
            author = author,
            coverUrl = coverUrl,
            dueDate = dueDate,
            status = "active",
            renewalCount = 0
        )

        // Usamos uma transação para garantir que a disponibilidade do livro diminua ao criar o empréstimo
        db.runTransaction { transaction ->
            val bookRef = db.collection("books").document(bookId)
            val bookSnapshot = transaction.get(bookRef)
            val available = bookSnapshot.getLong("available") ?: 0

            if (available <= 0) throw Exception("Livro indisponível para empréstimo")

            transaction.set(docRef, loan)
            transaction.update(bookRef, "available", available - 1)
            
            if (available - 1 <= 0) {
                transaction.update(bookRef, "status", "unavailable")
            }

            // Cria uma notificação automática de confirmação
            val notificationRef = db.collection("users").document(userId).collection("notifications").document()
            val notification = mapOf(
                "id" to notificationRef.id,
                "title" to "Empréstimo Realizado!",
                "message" to "Você pegou '$title'. Devolução até $dueDate.",
                "type" to "loan",
                "timestamp" to System.currentTimeMillis(),
                "isRead" to false
            )
            transaction.set(notificationRef, notification)
        }.await()
        
        dueDate
    }

    suspend fun returnBook(userId: String, bookId: String): Result<Unit> = runCatching {
        // 1. Localizar o empréstimo ativo
        val loanSnapshot = db.collection(loansCollection)
            .whereEqualTo("userId", userId)
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("status", "active")
            .get()
            .await()

        if (loanSnapshot.isEmpty) throw Exception("Nenhum empréstimo ativo encontrado para este livro")

        val loanDoc = loanSnapshot.documents.first()

        // 2. Transação para devolver e atualizar estoque
        db.runTransaction { transaction ->
            val bookRef = db.collection("books").document(bookId)
            val bookSnapshot = transaction.get(bookRef)
            val available = bookSnapshot.getLong("available") ?: 0
            val total = bookSnapshot.getLong("total") ?: 1

            // Atualiza status do empréstimo
            transaction.update(loanDoc.reference, "status", "returned")
            transaction.update(loanDoc.reference, "returnDate", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))

            // Devolve exemplar ao estoque
            val newAvailable = (available + 1).coerceAtMost(total)
            transaction.update(bookRef, "available", newAvailable)
            transaction.update(bookRef, "status", "available")

            // Notifica o sistema de que houve uma devolução (para reservas)
            // Aqui poderíamos chamar notifyNextInQueue se quiséssemos automatizar
        }.await()
    }
}
