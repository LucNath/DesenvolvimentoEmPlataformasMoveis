package com.bibliotecadigital.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SupportViewModel : ViewModel() {
    private val repository = SupportRepository()

    private val _supportResult = MutableLiveData<Boolean>()
    val supportResult: LiveData<Boolean> = _supportResult

    fun sendFeedback(category: String, description: String) {
        repository.sendFeedback(category, description) { success ->
            _supportResult.postValue(success)
        }
    }
}