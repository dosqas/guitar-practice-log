package com.dosqas.guitarpracticelog

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.ExperimentalTime

val sharedViewModel = PracticeViewModel()

class PracticeViewModel {
    private var nextId = 4

    @OptIn(ExperimentalTime::class)
    private val _sessions = MutableStateFlow(
        listOf(
            PracticeSession(1, "Nothing Else Matters", Clock.System.todayIn(TimeZone.currentSystemDefault()), 30, "Chords", "Focused on smooth transitions."),
            PracticeSession(2, "Sweet Child O' Mine", Clock.System.todayIn(TimeZone.currentSystemDefault()), 45, "Solo", "Worked on accuracy."),
            PracticeSession(3, "Smoke on the Water", Clock.System.todayIn(TimeZone.currentSystemDefault()), 20, "Riff")
        )
    )
    val sessions: StateFlow<List<PracticeSession>> = _sessions.asStateFlow()

    fun addSession(songTitle: String, date: LocalDate, duration: Int, focus: String, notes: String?) {
        val currentList = _sessions.value
        val newSession = PracticeSession(nextId++, songTitle, date, duration, focus, notes)
        _sessions.value = currentList + newSession
    }

    fun updateSession(updated: PracticeSession) {
        val currentList = _sessions.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            currentList[index] = updated
            _sessions.value = currentList
        }
    }

    fun deleteSession(id: Int) {
        _sessions.value = _sessions.value.filterNot { it.id == id }
    }
}
