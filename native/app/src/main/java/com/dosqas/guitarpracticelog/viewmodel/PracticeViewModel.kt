package com.dosqas.guitarpracticelog.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosqas.guitarpracticelog.data.model.PracticeSession
import com.dosqas.guitarpracticelog.data.repository.PracticeRepository
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.flow.catch

class PracticeViewModel(val repository: PracticeRepository) : ViewModel() {

    // Sessions retrieved once and exposed as LiveData
    private val _sessions = mutableStateOf<List<PracticeSession>>(emptyList())
    val sessionsState: State<List<PracticeSession>> = _sessions

    init {
        viewModelScope.launch {
            repository.getAllSessions()
                .catch { e ->
                    Log.e("PracticeViewModel", "Fetching sessions failed", e)
                    _errorMessage.value = "Failed to load sessions: ${e.message}"
                }
                .collect { list ->
                    _sessions.value = list
                }
        }
    }


    // Error state observed by the UI
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Success state observed by the UI
    private val _success = mutableStateOf(false)
    val success: State<Boolean> = _success

    fun insertSession(session: PracticeSession) = viewModelScope.launch {
        try {
            repository.insertSession(session)
            _errorMessage.value = null
            _success.value = true
        } catch (e: Exception) {
            Log.e("PracticeViewModel", "Insert failed", e)
            _errorMessage.value = "Failed to add session: ${e.message}"
            _success.value = false
        }
    }

    fun updateSession(session: PracticeSession) = viewModelScope.launch {
        try {
            repository.updateSession(session)
            _errorMessage.value = null
            _success.value = true
        } catch (e: Exception) {
            Log.e("PracticeViewModel", "Update failed", e)
            _errorMessage.value = "Failed to update session: ${e.message}"
            _success.value = false
        }
    }

    fun deleteSession(sessionId: Int) = viewModelScope.launch {
        try {
            repository.deleteSession(sessionId)
            _errorMessage.value = null
        } catch (e: Exception) {
            Log.e("PracticeViewModel", "Delete failed", e)
            _errorMessage.value = "Failed to delete session: ${e.message}"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _success.value = false
    }
}
