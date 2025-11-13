package com.dosqas.guitarpracticelog.data.repository

import android.util.Log
import com.dosqas.guitarpracticelog.data.db.PracticeDao
import com.dosqas.guitarpracticelog.data.model.PracticeSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class PracticeRepositoryImpl(private val dao: PracticeDao) : PracticeRepository {

    override fun getAllSessions(): Flow<List<PracticeSession>> =
        dao.getAllSessions()
            .catch { e ->
                Log.e("Repository", "Fetch sessions failed", e)
                throw e
            }

    override suspend fun insertSession(session: PracticeSession) {
        try {
            dao.insertSession(session)
        } catch (e: Exception) {
            Log.e("Repository", "Insert failed", e)
            throw e
        }
    }

    override suspend fun updateSession(session: PracticeSession) {
        try {
            dao.updateSession(session)
        } catch (e: Exception) {
            Log.e("Repository", "Update failed", e)
            throw e
        }
    }

    override suspend fun deleteSession(sessionId: Int) {
        try {
            dao.deleteSession(sessionId)
        } catch (e: Exception) {
            Log.e("Repository", "Delete failed", e)
            throw e
        }
    }
}
