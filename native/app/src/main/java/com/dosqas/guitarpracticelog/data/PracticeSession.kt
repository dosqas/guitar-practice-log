package com.dosqas.guitarpracticelog.data

import java.time.LocalDate

data class PracticeSession(
    val id: Int,
    val songTitle: String,
    val date: LocalDate,
    val durationMinutes: Int,
    val focusArea: String,
    val notes: String? = null
)