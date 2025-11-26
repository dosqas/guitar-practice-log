package com.dosqas.guitarpracticelog.data.remote

import com.dosqas.guitarpracticelog.data.model.PracticeSession
import com.dosqas.guitarpracticelog.data.model.SyncStatus
import java.time.LocalDate

data class PracticeSessionResponseDto(
    val id: Int = 0,
    val songTitle: String,
    val date: LocalDate,
    val durationMinutes: Int,
    val focusArea: String,
    val notes: String? = null
)

// Mapper: DTO -> Entity (for saving locally)
fun PracticeSessionResponseDto.toEntity(status: SyncStatus = SyncStatus.SYNCED): PracticeSession {
    return PracticeSession(
        id = this.id,
        songTitle = this.songTitle,
        date = this.date,
        durationMinutes = this.durationMinutes,
        focusArea = this.focusArea,
        notes = this.notes,
        status = status
    )
}