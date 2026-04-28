package com.bibliotecadigital.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ReadingGoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReadingGoalRepository(application)
    private val _goals = MutableLiveData<List<ReadingGoal>>()
    val goals: LiveData<List<ReadingGoal>> = _goals

    init {
        refresh()
    }

    fun refresh() {
        _goals.value = repository.getGoals()
    }

    fun addGoal(title: String, quantity: Int, deadline: String) {
        val currentList = _goals.value?.toMutableList() ?: mutableListOf()
        val newGoal = ReadingGoal(
            id = System.currentTimeMillis().toString(),
            title = title,
            quantity = quantity,
            deadline = deadline,
            progress = 0,
            status = GoalStatus.EM_ANDAMENTO
        )
        currentList.add(0, newGoal)
        updateList(currentList)
    }

    fun incrementProgress(goalId: String) {
        val currentList = _goals.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == goalId }
        if (index != -1) {
            val goal = currentList[index]
            if (goal.progress < goal.quantity) {
                val newProgress = goal.progress + 1
                val newStatus = if (newProgress >= goal.quantity) GoalStatus.CONCLUIDA else goal.status
                currentList[index] = goal.copy(progress = newProgress, status = newStatus)
                updateList(currentList)
            }
        }
    }

    fun deleteGoal(goalId: String) {
        val currentList = _goals.value?.toMutableList() ?: return
        currentList.removeAll { it.id == goalId }
        updateList(currentList)
    }

    private fun updateList(newList: List<ReadingGoal>) {
        _goals.value = newList
        repository.saveGoals(newList)
    }
}