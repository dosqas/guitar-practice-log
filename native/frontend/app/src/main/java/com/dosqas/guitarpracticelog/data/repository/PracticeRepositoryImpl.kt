package com.dosqas.guitarpracticelog.data.repository

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dosqas.guitarpracticelog.data.db.PracticeDao
import com.dosqas.guitarpracticelog.data.model.PracticeSession
import com.dosqas.guitarpracticelog.data.model.SyncStatus
import com.dosqas.guitarpracticelog.data.remote.PracticeApi
import com.dosqas.guitarpracticelog.data.remote.toRequestDto
import com.dosqas.guitarpracticelog.data.remote.toEntity
import com.dosqas.guitarpracticelog.worker.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import okio.IOException
import java.util.concurrent.TimeUnit

class PracticeRepositoryImpl(
    private val dao: PracticeDao,
    private val api: PracticeApi,
    private val appContext: Context
) : PracticeRepository {
    private suspend fun <T> withApiTimeout(block: suspend () -> T): T {
        return withTimeout(300) {
            block()
        }
    }

    private var cachedSessions: List<PracticeSession>? = null

    override fun getAllSessions(): Flow<List<PracticeSession>> = flow {
        if (cachedSessions == null) {
            try {
                val serverSessions = withApiTimeout { api.getAllSessions() }
                val localSessions = dao.getAllSessionsOnce()

                // Keep only local sessions that are not synced (pending)
                val pendingLocal = localSessions.filter { it.status != SyncStatus.SYNCED }

                // Clear local cache for synced sessions
                dao.getAllSessionsOnce()
                    .filter { it.status == SyncStatus.SYNCED }
                    .forEach { dao.deleteSession(it.id) }

                // Insert server sessions as SYNCED
                serverSessions.forEach { dao.insertSession(it.copy(status = SyncStatus.SYNCED)) }

                // Re-insert pending local sessions
                pendingLocal.forEach { dao.insertSession(it) }

                cachedSessions = dao.getAllSessionsOnce()
                Log.d("Repository", "Server sessions cached with pending sessions preserved")
            } catch (e: Exception) {
                Log.w("Repository", "Server unavailable, using local DB", e)
                cachedSessions = dao.getAllSessionsOnce().filter { it.status != SyncStatus.SYNCED }
            }
        }

        emitAll(dao.getAllSessions())
    }

    override suspend fun insertSession(session: PracticeSession) {
        try {
            // Step 1: Try to create directly on the server first
            try {
                val dto = session.toRequestDto()
                val createdDto = withApiTimeout {
                    api.createSession(dto)
                }

                // Step 2: Save server-confirmed session locally (already has correct ID)
                dao.insertSession(createdDto.toEntity(status = SyncStatus.SYNCED))
                Log.d("Repository", "Session created on server and stored locally (id=${createdDto.id})")

            } catch (e: Exception) {
                // Step 3: Couldn’t reach server - fallback to local
                val existing = dao.getSessionByTitle(session.songTitle)
                if (existing != null && existing.status == SyncStatus.WAITING_FOR_DELETE) {
                    // It's pending deletion; remove it locally to free the title
                    dao.deleteSession(existing.id)
                }

                val local = session.copy(status = SyncStatus.WAITING_FOR_ADD)
                val localId = dao.insertSession(local)
                scheduleSync()
                Log.w("Repository", "Server unavailable, queued for later sync (localId=$localId)", e)
                throw IOException()
            }

        } catch (e: Exception) {
            Log.e("Repository", "Insert operation failed completely", e)
            throw e
        }
    }

    override suspend fun updateSession(session: PracticeSession) {
        try {
            // Step 1: Mark as waiting for update locally
            val pendingSession = session.copy(status = SyncStatus.WAITING_FOR_UPDATE)
            dao.updateSession(pendingSession)
            Log.d("Repository", "Updated session locally (status=WAITING_FOR_UPDATE): ${session.id}")

            // Step 2: Try syncing with the server
            try {
                val dto = session.toRequestDto()
                val updatedServerSessionDto = withApiTimeout {
                    api.updateSession(session.id, dto)
                }
                dao.updateSession(updatedServerSessionDto.toEntity(status = SyncStatus.SYNCED))
                Log.d("Repository", "Updated session on server: ${updatedServerSessionDto.id}")

            } catch (e: Exception) {
                // Server unavailable, we already marked it as waiting
                Log.w("Repository", "Server unavailable, session queued for sync: ${session.id}", e)
                scheduleSync()
                throw IOException()
            }

        } catch (e: Exception) {
            Log.e("Repository", "Update failed for ${session.id}", e)
            throw e
        }
    }

    override suspend fun deleteSession(sessionId: Int) {
        try {
            // Step 1: Get session from DB (to preserve info if needed)
            val session = dao.getSessionById(sessionId)

            // Step 2: Try deleting it on the server
            try {
                withApiTimeout {
                    api.deleteSession(sessionId)
                }
                Log.d("Repository", "Deleted session on server: $sessionId")

                // Step 3: If success -> delete locally
                dao.deleteSession(sessionId)
                Log.d("Repository", "Deleted session locally: $sessionId")

            } catch (e: Exception) {
                // Step 4: If offline -> mark as waiting for delete
                Log.w("Repository", "Server unavailable, marking for later delete: $sessionId", e)
                dao.updateSession(session.copy(status = SyncStatus.WAITING_FOR_DELETE))
                scheduleSync()
                throw IOException()
            }

        } catch (e: Exception) {
            Log.e("Repository", "Delete failed", e)
            throw e
        }
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                10, TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(appContext)
            .enqueueUniqueWork(
                "sync_work",
                ExistingWorkPolicy.KEEP,
                request
            )
    }
}
