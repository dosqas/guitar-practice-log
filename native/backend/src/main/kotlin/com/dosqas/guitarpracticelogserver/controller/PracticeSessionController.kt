package com.dosqas.guitarpracticelogserver.controller

import com.dosqas.guitarpracticelogserver.model.PracticeSession
import com.dosqas.guitarpracticelogserver.service.PracticeSessionService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sessions")
class PracticeSessionController(private val service: PracticeSessionService) {

    private val logger = LoggerFactory.getLogger(PracticeSessionController::class.java)

    @GetMapping
    fun getAll(): List<PracticeSession> {
        logger.info("GET /sessions called")
        val sessions = service.getAllSessions()
        logger.debug("Returning ${sessions.size} sessions")
        return sessions
    }

    @PostMapping
    fun create(@RequestBody session: PracticeSession): PracticeSession {
        logger.info("POST /sessions called with body: $session")
        val created = service.createSession(session)
        logger.debug("Created session with id: ${created.id}")
        return created
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody session: PracticeSession): PracticeSession? {
        logger.info("PUT /sessions/$id called")
        val updated = service.updateSession(id, session)
        logger.debug("Updated session: {}", updated)
        return updated
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        logger.info("DELETE /sessions/$id called")
        service.deleteSession(id)
        logger.debug("Deleted session with id: $id")
    }
}