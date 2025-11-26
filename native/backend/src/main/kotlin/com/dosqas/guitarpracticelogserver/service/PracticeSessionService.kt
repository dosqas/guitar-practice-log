package com.dosqas.guitarpracticelogserver.service

import com.dosqas.guitarpracticelogserver.model.PracticeSession
import com.dosqas.guitarpracticelogserver.repository.PracticeSessionRepository
import com.dosqas.guitarpracticelogserver.ws.SessionWebSocketHandler
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

data class WsEvent(val type: String, val payload: Any)

@Service
class PracticeSessionService(
    private val repository: PracticeSessionRepository,
    private val wsHandler: SessionWebSocketHandler
) {
    private val logger = LoggerFactory.getLogger(PracticeSessionService::class.java)

    private val objectMapper = jacksonObjectMapper().apply {
        // Register module for Java 8 Date/Time types
        registerModule(JavaTimeModule())
        // Optionally serialize dates as ISO strings
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private fun broadcast(type: String, payload: Any) {
        val json = objectMapper.writeValueAsString(WsEvent(type, payload))
        wsHandler.broadcast(json)
    }

    fun getAllSessions(): List<PracticeSession> {
        logger.info("Fetching all practice sessions")
        return repository.findAll()
    }

    fun getSessionById(id: Long): PracticeSession? {
        logger.info("Fetching session with id: $id")
        return repository.findById(id).orElse(null)
    }

    fun createSession(session: PracticeSession): PracticeSession {
        logger.info("Creating new session: ${session.songTitle}.")
        val saved = repository.save(session)
        logger.info("Session created with id: ${saved.id}")

        broadcast("SESSION_ADDED", saved)

        return saved
    }

    fun updateSession(id: Long, updated: PracticeSession): PracticeSession? {
        logger.info("Updating session with id: $id")

        val existing = getSessionById(id) ?: run {
            logger.info("Update skipped — session $id does not exist.")
            return null
        }

        val newSession = existing.copy(
            songTitle = updated.songTitle,
            date = updated.date,
            durationMinutes = updated.durationMinutes,
            focusArea = updated.focusArea,
            notes = updated.notes
        )

        val saved = repository.save(newSession)
        logger.info("Session updated successfully: $saved")

        broadcast("SESSION_UPDATED", saved)

        return saved
    }

    fun deleteSession(id: Long) {
        logger.info("Deleting session with id: $id")

        if (!repository.existsById(id)) {
            logger.info("Delete skipped - session $id does not exist.")
            return
        }

        repository.deleteById(id)

        logger.info("Session deleted successfully: $id")

        broadcast("SESSION_DELETED", id)
    }
}
