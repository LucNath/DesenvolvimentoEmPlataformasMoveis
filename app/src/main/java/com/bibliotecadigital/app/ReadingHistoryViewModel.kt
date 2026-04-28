package com.bibliotecadigital.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReadingHistoryViewModel : ViewModel() {

    private val _historyEntries = MutableLiveData<List<ReadingHistoryEntry>>()
    val historyEntries: LiveData<List<ReadingHistoryEntry>> = _historyEntries

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockData = listOf(
            ReadingHistoryEntry("1", "Clean Code", "Robert C. Martin", "12/10/2023", R.drawable.bg_cover_placeholder),
            ReadingHistoryEntry("2", "Design Patterns", "Erich Gamma", "05/10/2023", R.drawable.bg_cover_placeholder),
            ReadingHistoryEntry("3", "The Pragmatic Programmer", "Andrew Hunt", "20/09/2023", R.drawable.bg_cover_placeholder),
            ReadingHistoryEntry("4", "Refactoring", "Martin Fowler", "10/09/2023", R.drawable.bg_cover_placeholder),
            ReadingHistoryEntry("5", "Introduction to Algorithms", "Thomas H. Cormen", "01/09/2023", R.drawable.bg_cover_placeholder)
        )
        _historyEntries.value = mockData
    }
}