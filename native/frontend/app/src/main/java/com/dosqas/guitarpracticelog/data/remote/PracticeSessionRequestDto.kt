package com.dosqas.guitarpracticelog.data.remote

import com.dosqas.guitarpracticelog.data.model.PracticeSession
import java.time.LocalDate

data class PracticeSessionRequestDto(
    val songTitle: String,
    val date: LocalDate,
    val durationMinutes: Int,
    val focusArea: String,
    val notes: String? = null
)

fun PracticeSession.toRequestDto(): PracticeSessionRequestDto {
    return PracticeSessionRequestDto(
        songTitle = this.songTitle,
        date = this.date,
        durationMinutes = this.durationMinutes,
        focusArea = this.focusArea,
        notes = this.notes
    )
}
