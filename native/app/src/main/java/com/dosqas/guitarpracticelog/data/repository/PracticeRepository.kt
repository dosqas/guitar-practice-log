package com.dosqas.guitarpracticelog.data.repository

import com.dosqas.guitarpracticelog.data.model.PracticeSession
import kotlinx.coroutines.flow.Flow

interface PracticeRepository {
    fun getAllSessions(): Flow<List<PracticeSession>>
    suspend fun insertSession(session: PracticeSession)
    suspend fun updateSession(session: PracticeSession)
    suspend fun deleteSession(sessionId: Int)
}
