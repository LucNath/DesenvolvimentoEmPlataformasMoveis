package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadigital.app.repository.EventRepository
import com.bibliotecadigital.app.entity.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val eventRepository = EventRepository(FirebaseFirestore.getInstance())

    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage

    val events: StateFlow<List<Event>> = combine(_allEvents, _selectedDate) { events, date ->
        if (date.isEmpty() || date == "Todas") {
            events
        } else {
            events.filter { it.date == date }
        }
    }.stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            eventRepository.getEvents().collect { eventsList ->
                _allEvents.value = eventsList
                _isLoading.value = false
            }
        }
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun enrollEvent(eventId: String, userId: String) {
        viewModelScope.launch {
            val result = eventRepository.enrollEvent(eventId, userId)
            result.onFailure { _actionMessage.value = it.message }
            result.onSuccess { _actionMessage.value = "Inscrição realizada com sucesso!" }
        }
    }

    fun cancelEnrollment(eventId: String, userId: String) {
        viewModelScope.launch {
            val result = eventRepository.cancelEnrollment(eventId, userId)
            result.onFailure { _actionMessage.value = it.message }
            result.onSuccess { _actionMessage.value = "Inscrição cancelada." }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}