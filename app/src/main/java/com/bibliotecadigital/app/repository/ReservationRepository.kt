package com.bibliotecadigital.app.repository

import com.bibliotecadigital.app.entity.Notification
import com.bibliotecadigital.app.entity.NotificationType
import com.bibliotecadigital.app.entity.Reservation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FirestoreRepository(db) {

    private val reservationsCollection = "reservations"
    private val usersCollection = "users"

    // Lista reservas ativas de um usuário em tempo real
    fun getReservations(userId: String): Flow<List<Reservation>> {
        return getFilteredCollection(
            reservationsCollection,
            Reservation::class.java,
            "userId" to userId,
            "status" to "active"
        )
    }

    // Cria uma reserva calculando a posição na fila
    suspend fun createReservation(
        userId: String,
        bookId: String,
        title: String,
        author: String,
        coverUrl: String
    ): Result<Unit> = runCatching {
        // Conta quantas reservas ativas existem para esse livro
        val existingReservations = db.collection(reservationsCollection)
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("status", "active")
            .get()
            .await()

        val queuePosition = existingReservations.size() + 1

        val reservationId = db.collection(reservationsCollection).document().id

        val reservation = Reservation(
            id = reservationId,
            userId = userId,
            bookId = bookId,
            title = title,
            author = author,
            coverUrl = coverUrl,
            queuePosition = queuePosition,
            status = "active"
        )

        db.collection(reservationsCollection)
            .document(reservationId)
            .set(reservation)
            .await()
    }

    // Cancela uma reserva e reordena a fila dos demais
    suspend fun cancelReservation(reservationId: String, bookId: String): Result<Unit> = runCatching {
        val reservationRef = db.collection(reservationsCollection).document(reservationId)

        // Busca a posição atual da reserva cancelada
        val cancelledSnapshot = reservationRef.get().await()
        val cancelledPosition = cancelledSnapshot.getLong("queuePosition")?.toInt()
            ?: throw Exception("Reserva não encontrada")

        // Marca como cancelada
        reservationRef.update("status", "cancelled").await()

        // Reordena os demais: todos com queuePosition > cancelledPosition recuam 1
        val toReorder = db.collection(reservationsCollection)
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("status", "active")
            .whereGreaterThan("queuePosition", cancelledPosition)
            .get()
            .await()

        val batch = db.batch()
        for (doc in toReorder) {
            val currentPos = doc.getLong("queuePosition")?.toInt() ?: continue
            batch.update(doc.reference, "queuePosition", currentPos - 1)
        }
        batch.commit().await()
    }

    // Verifica se há reservas para o livro e notifica o próximo da fila
    // Chamar este método após confirmar a devolução de um livro
    suspend fun notifyNextInQueue(bookId: String): Result<Unit> = runCatching {
        val nextReservation = db.collection(reservationsCollection)
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("status", "active")
            .whereEqualTo("queuePosition", 1)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return@runCatching // Nenhuma reserva ativa, não faz nada

        val userId = nextReservation.getString("userId") ?: return@runCatching
        val title = nextReservation.getString("title") ?: "livro reservado"

        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val notificationId = db.collection(usersCollection)
            .document(userId)
            .collection("notifications")
            .document().id

        val notification = Notification(
            id = notificationId,
            message = "O livro \"$title\" está disponível para retirada!",
            type = NotificationType.RESERVATION_READY,
            timestamp = timestamp,
            isRead = false
        )

        db.collection(usersCollection)
            .document(userId)
            .collection("notifications")
            .document(notificationId)
            .set(notification)
            .await()
    }
}