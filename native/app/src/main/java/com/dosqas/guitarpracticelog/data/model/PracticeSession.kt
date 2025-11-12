package com.dosqas.guitarpracticelog.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "practice_sessions",
    indices = [Index(value = ["songTitle"], unique = true)]
)
data class PracticeSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val songTitle: String,
    val date: LocalDate,
    val durationMinutes: Int,
    val focusArea: String,
    val notes: String? = null
)