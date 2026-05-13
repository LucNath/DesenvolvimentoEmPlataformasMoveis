package com.bibliotecadigital.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val repository: NotificationRepository = NotificationRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val currentUserId: String?
        get() = authRepository.getCurrentUserUid()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.getNotifications(userId).collectLatest { list ->
                _notifications.value = list
            }
        }
    }

    fun markAsRead(notificationId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.markAsRead(userId, notificationId)
        }
    }

    fun markAllAsRead() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.markAllAsRead(userId)
        }
    }

    fun deleteNotification(notificationId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.deleteNotification(userId, notificationId)
        }
    }
}
