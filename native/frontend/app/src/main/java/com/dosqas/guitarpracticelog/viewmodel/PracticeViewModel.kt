package com.dosqas.guitarpracticelog.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosqas.guitarpracticelog.data.model.PracticeSession
import com.dosqas.guitarpracticelog.data.repository.PracticeRepository
import kotlinx.coroutines.launch
import android.util.Log
import com.dosqas.guitarpracticelog.data.remote.RetrofitInstance.api
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import retrofit2.HttpException

class PracticeViewModel(val repository: PracticeRepository) : ViewModel() {

    // Error state observed by the UI
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _networkError = mutableStateOf<String?>(null)
    val networkError: State<String?> = _networkError

    // Success state observed by the UI
    private val _success = mutableStateOf(false)
    val success: State<Boolean> = _success

    // Sessions retrieved once and exposed as LiveData
    private val _sessions = mutableStateOf<List<PracticeSession>>(emptyList())
    val sessionsState: State<List<PracticeSession>> = _sessions

    init {
        viewModelScope.launch {
            repository.getAllSessions()
                .catch { e ->
                    Log.e("PracticeViewModel", "Fetching sessions failed", e)

                    _errorMessage.value = when (e) {
                        is IOException -> "Cannot reach server. Displaying local data."
                        else -> "Failed to load sessions: please try again."
                    }
                }
                .collect { list ->
                    _sessions.value = list
                }
        }
    }

    val networkStatusFlow = flow {
        while (true) {
            try {
                api.pingServer()
                emit(null)
            } catch (_: Exception) {
                emit("Server is down. Showing saved local data.")
            }
            delay(5_000L)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun insertSession(session: PracticeSession) = viewModelScope.launch {
        try {
            repository.insertSession(session)
            _errorMessage.value = null
            _success.value = true
        } catch (e: Exception) {
            Log.e("PracticeViewModel", "Insert failed", e)
            _success.value = false

            _errorMessage.value = when (e) {
                is IOException -> "Cannot reach server temporarily. The operation is saved and will sync when online."
                is HttpException -> "Server error (${e.code()}). Please try again later."
                else -> "Unexpected error occurred. Please try again."
            }
        }
    }

    fun updateSession(session: PracticeSession) = viewModelScope.launch {
        try {
            repository.updateSession(session)
            _errorMessage.value = null
            _success.value = true
        } catch (e: Exception) {
            Log.e("PracticeViewModel", "Update failed", e)
            _success.value = false

            _errorMessage.value = when (e) {
                is IOException -> "Cannot reach server temporarily. The operation is saved and will sync when online."
                is HttpException -> "Server error (${e.code()}). Please try again later."
                else -> "Unexpected error occurred. Please try again."
            }
        }
    }

    fun deleteSession(sessionId: Int) = viewModelScope.launch {
        try {
            repository.deleteSession(sessionId)
            _errorMessage.value = null
        } catch (e: Exception) {
            Log.e("PracticeViewModel", "Delete failed", e)

            _errorMessage.value = when (e) {
                is IOException -> "Cannot reach server temporarily. The operation is saved and will sync when online."
                is HttpException -> "Server error (${e.code()}). Please try again later."
                else -> "Unexpected error occurred. Please try again."
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _success.value = false
    }

    fun clearNetworkError() {
        _networkError.value = null
    }
}
