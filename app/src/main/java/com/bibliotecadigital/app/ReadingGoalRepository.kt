package com.bibliotecadigital.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReadingGoalRepository(context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "reading_goals"

    fun getGoals(): List<ReadingGoal> {
        val json = prefs.getString(key, null) ?: return loadMockGoals()
        val type = object : TypeToken<List<ReadingGoal>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveGoals(goals: List<ReadingGoal>) {
        val json = gson.toJson(goals)
        prefs.edit().putString(key, json).apply()
    }

    private fun loadMockGoals(): List<ReadingGoal> {
        val mocks = listOf(
            ReadingGoal("1", "Meta de Verão", 5, "31/01/2024", 3, GoalStatus.EM_ANDAMENTO),
            ReadingGoal("2", "Leituras 2024", 12, "31/12/2024", 12, GoalStatus.CONCLUIDA),
            ReadingGoal("3", "Desafio Mensal", 3, "01/01/2024", 1, GoalStatus.ATRASADA)
        )
        saveGoals(mocks)
        return mocks
    }
}