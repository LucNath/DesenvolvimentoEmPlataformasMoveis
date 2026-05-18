package com.bibliotecadigital.app

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class EventRepository(private val db: FirebaseFirestore) : FirestoreRepository(db) {

    private val eventsCollection = "events"

    fun getEvents(): Flow<List<Event>> {
        return getCollection(eventsCollection, Event::class.java)
    }

    fun getEventsByDate(date: String): Flow<List<Event>> {
        return getFilteredCollection(eventsCollection, Event::class.java, "date" to date)
    }

    suspend fun enrollEvent(eventId: String, userId: String): Result<Unit> = runCatching {
        val eventRef = db.collection(eventsCollection).document(eventId)
        
        db.runTransaction { transaction ->
            val snapshot = transaction.get(eventRef)
            val event = snapshot.toObject(Event::class.java) ?: throw Exception("Evento não encontrado")

            if (event.usedSlots >= event.totalSlots) {
                throw Exception("Evento lotado")
            }
            if (event.participants.contains(userId)) {
                throw Exception("Usuário já inscrito")
            }

            transaction.update(eventRef, "usedSlots", FieldValue.increment(1))
            transaction.update(eventRef, "participants", FieldValue.arrayUnion(userId))
        }.await()
    }

    suspend fun cancelEnrollment(eventId: String, userId: String): Result<Unit> = runCatching {
        val eventRef = db.collection(eventsCollection).document(eventId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(eventRef)
            val event = snapshot.toObject(Event::class.java) ?: throw Exception("Evento não encontrado")

            if (!event.participants.contains(userId)) {
                throw Exception("Usuário não está inscrito neste evento")
            }

            transaction.update(eventRef, "usedSlots", FieldValue.increment(-1))
            transaction.update(eventRef, "participants", FieldValue.arrayRemove(userId))
        }.await()
    }
}