package com.dosqas.guitarpracticelogserver.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "practice_sessions",
    indexes = [Index(name = "idx_song_title", columnList = "songTitle", unique = true)]
)
data class PracticeSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val songTitle: String,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(nullable = false)
    val durationMinutes: Int,

    @Column(nullable = false)
    val focusArea: String,

    val notes: String? = null
)
