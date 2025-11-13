package com.dosqas.guitarpracticelog.data.db

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toDate(dateString: String): LocalDate = LocalDate.parse(dateString)
}
