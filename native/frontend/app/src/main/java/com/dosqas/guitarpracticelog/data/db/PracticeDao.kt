package com.dosqas.guitarpracticelog.data.db

import androidx.room.*
import com.dosqas.guitarpracticelog.data.model.PracticeSession
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeDao {
    @Query("SELECT * FROM practice_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>

    @Insert
    suspend fun insertSession(session: PracticeSession)

    @Update
    suspend fun updateSession(session: PracticeSession)

    @Query("DELETE FROM practice_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Int)
}
