package com.dosqas.guitarpracticelog.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dosqas.guitarpracticelog.data.model.PracticeSession

@Database(entities = [PracticeSession::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun practiceDao(): PracticeDao
}
