package com.dosqas.guitarpracticelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dosqas.guitarpracticelog.data.PracticeSession
import java.time.LocalDate

class PracticeViewModel : ViewModel() {
    private var nextId = 4  // since we already have 3 hardcoded sessions

    // MutableLiveData holds the data that can change
    private val _sessions = MutableLiveData(
        listOf(
            PracticeSession(1, "Nothing Else Matters", LocalDate.now(), 30, "Chords", "Focused on smooth transitions."),
            PracticeSession(2, "Sweet Child O' Mine", LocalDate.now(), 45, "Solo", "Worked on accuracy."),
            PracticeSession(3, "Smoke on the Water", LocalDate.now(), 20, "Riff")
        )
    )

    // Publicly exposed as LiveData (read-only)
    val sessions: LiveData<List<PracticeSession>> = _sessions

    // Add a new session
    fun addSession(songTitle: String, date: LocalDate, duration: Int, focus: String, notes: String?) {
        val currentList = _sessions.value ?: emptyList()
        val newSession = PracticeSession(nextId++, songTitle, date, duration, focus, notes)
        _sessions.value = currentList + newSession  // creates a new list with the added item
    }

    // Update a session
    fun updateSession(updated: PracticeSession) {
        val currentList = _sessions.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            currentList[index] = updated
            _sessions.value = currentList
        }
    }

    // Delete a session
    fun deleteSession(id: Int) {
        val currentList = _sessions.value?.toMutableList() ?: return
        currentList.removeAll { it.id == id }
        _sessions.value = currentList
    }
}
