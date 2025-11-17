package com.dosqas.guitarpracticelog.data.db

import androidx.room.*
import com.dosqas.guitarpracticelog.data.model.PracticeSession
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeDao {
    @Query("SELECT * FROM practice_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>

    @Query("SELECT * FROM practice_sessions")
    suspend fun getAllSessionsOnce(): List<PracticeSession>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSession)

    @Update
    suspend fun updateSession(session: PracticeSession)

    @Query("DELETE FROM practice_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Int)

    @Query("SELECT * FROM practice_sessions WHERE id = :id")
    suspend fun getSessionById(id: Int): PracticeSession

    @Query("SELECT * FROM practice_sessions WHERE status != 'SYNCED' ORDER BY id ASC")
    suspend fun getPendingSessionsOnce(): List<PracticeSession>

    @Query("SELECT * FROM practice_sessions WHERE songTitle = :songTitle")
    suspend fun getSessionByTitle(songTitle: String): PracticeSession
}
