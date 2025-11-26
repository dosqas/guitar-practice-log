package com.dosqas.guitarpracticelogserver.repository

import com.dosqas.guitarpracticelogserver.model.PracticeSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PracticeSessionRepository : JpaRepository<PracticeSession, Long>
