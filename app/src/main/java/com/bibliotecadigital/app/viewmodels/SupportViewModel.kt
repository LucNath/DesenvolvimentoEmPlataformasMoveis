package com.bibliotecadigital.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bibliotecadigital.app.NetworkMonitor
import com.bibliotecadigital.app.repository.SupportRepository

class SupportViewModel(private val networkMonitor: NetworkMonitor? = null) : ViewModel() {
    private val repository = SupportRepository()

    private val _supportResult = MutableLiveData<Boolean>()
    val supportResult: LiveData<Boolean> = _supportResult

    val isConnected = networkMonitor?.isConnected?.asLiveData()

    fun sendFeedback(category: String, description: String) {
        repository.sendFeedback(category, description) { success ->
            _supportResult.postValue(success)
        }
    }
}