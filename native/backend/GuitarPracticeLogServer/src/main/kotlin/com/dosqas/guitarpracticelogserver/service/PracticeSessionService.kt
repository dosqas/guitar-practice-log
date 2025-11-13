package com.dosqas.guitarpracticelogserver.service

import com.dosqas.guitarpracticelogserver.model.PracticeSession
import com.dosqas.guitarpracticelogserver.repository.PracticeSessionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PracticeSessionService(private val repository: PracticeSessionRepository) {

    private val logger = LoggerFactory.getLogger(PracticeSessionService::class.java)

    fun getAllSessions(): List<PracticeSession> {
        logger.info("Fetching all practice sessions")
        val sessions = repository.findAll()
        logger.debug("Found ${sessions.size} sessions")
        return sessions
    }

    fun getSessionById(id: Long): PracticeSession {
        logger.info("Fetching session with id: $id")
        return repository.findById(id).orElseThrow {
            logger.error("Session with id $id not found")
            RuntimeException("Not found")
        }
    }

    fun createSession(session: PracticeSession): PracticeSession {
        logger.info("Creating new session: ${session.songTitle}")
        val saved = repository.save(session)
        logger.debug("Session created with id: ${saved.id}")
        return saved
    }

    fun updateSession(id: Long, updated: PracticeSession): PracticeSession {
        logger.info("Updating session with id: $id")
        val existing = getSessionById(id)
        val newSession = existing.copy(
            songTitle = updated.songTitle,
            date = updated.date,
            durationMinutes = updated.durationMinutes,
            focusArea = updated.focusArea,
            notes = updated.notes
        )
        val saved = repository.save(newSession)
        logger.debug("Session updated successfully: {}", saved)
        return saved
    }

    fun deleteSession(id: Long) {
        logger.info("Deleting session with id: $id")
        repository.deleteById(id)
        logger.debug("Session deleted successfully: $id")
    }
}
