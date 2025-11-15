package com.dosqas.guitarpracticelog.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dosqas.guitarpracticelog.data.db.AppDatabase
import com.dosqas.guitarpracticelog.data.model.SyncStatus
import com.dosqas.guitarpracticelog.data.remote.RetrofitInstance
import com.dosqas.guitarpracticelog.data.remote.toRequestDto
import com.dosqas.guitarpracticelog.data.remote.toEntity

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val dao = AppDatabase.getInstance(context).practiceDao()

    override suspend fun doWork(): Result {
        val pending = dao.getPendingSessionsOnce()
        var allSynced = true

        for (session in pending) {
            try {
                when (session.status) {
                    SyncStatus.WAITING_FOR_ADD -> {
                        val created = RetrofitInstance.api.createSession(session.toRequestDto())
                        dao.deleteSession(session.id)
                        dao.insertSession(created.toEntity(SyncStatus.SYNCED))
                    }
                    SyncStatus.WAITING_FOR_UPDATE -> {
                        val updated = RetrofitInstance.api.updateSession(session.id, session.toRequestDto())
                        if (updated == null) dao.deleteSession(session.id)
                        dao.updateSession(updated.toEntity(SyncStatus.SYNCED))
                    }
                    SyncStatus.WAITING_FOR_DELETE -> {
                        RetrofitInstance.api.deleteSession(session.id)
                        dao.deleteSession(session.id)
                    }
                    else -> {}
                }
            } catch (_: Exception) {
                allSynced = false
            }
        }

        return if (allSynced) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}

