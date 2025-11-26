package com.dosqas.guitarpracticelog.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dosqas.guitarpracticelog.data.model.PracticeSession

@Database(entities = [PracticeSession::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun practiceDao(): PracticeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {

                    // --- Migrations move here ---
                    val migration2to3 = object : Migration(2, 3) {
                        override fun migrate(db: SupportSQLiteDatabase) {
                            db.execSQL(
                                """
                                CREATE UNIQUE INDEX `index_practice_sessions_songTitle`
                                ON `practice_sessions` (`songTitle`)
                                """
                            )
                        }
                    }

                    val migration3to4 = object : Migration(3, 4) {
                        override fun migrate(db: SupportSQLiteDatabase) {
                            db.execSQL(
                                """
                                ALTER TABLE practice_sessions
                                ADD COLUMN status TEXT NOT NULL DEFAULT 'SYNCED'
                                """
                            )
                        }
                    }

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "practice_db3"
                    )
                        .addMigrations(migration2to3, migration3to4)
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
