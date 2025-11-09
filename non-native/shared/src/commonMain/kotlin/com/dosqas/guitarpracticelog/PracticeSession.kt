package com.dosqas.guitarpracticelog

import kotlinx.datetime.LocalDate

data class PracticeSession(
    val id: Int,
    val songTitle: String,
    val date: LocalDate,
    val durationMinutes: Int,
    val focusArea: String,
    val notes: String? = null
)