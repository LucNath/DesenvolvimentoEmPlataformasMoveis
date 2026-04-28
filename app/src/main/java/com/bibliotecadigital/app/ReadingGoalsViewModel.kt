package com.bibliotecadigital.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReadingGoalsViewModel : ViewModel() {

    private val _goals = MutableLiveData<List<ReadingGoal>>()
    val goals: LiveData<List<ReadingGoal>> = _goals

    init {
        loadMockGoals()
    }

    private fun loadMockGoals() {
        _goals.value = listOf(
            ReadingGoal("1", 5, "1 mês", 3),
            ReadingGoal("2", 12, "6 meses", 7)
        )
    }

    fun addGoal(quantity: Int, period: String) {
        val currentList = _goals.value?.toMutableList() ?: mutableListOf()
        val newGoal = ReadingGoal(
            id = System.currentTimeMillis().toString(),
            quantity = quantity,
            period = period,
            progress = 0
        )
        currentList.add(0, newGoal)
        _goals.value = currentList
    }
}