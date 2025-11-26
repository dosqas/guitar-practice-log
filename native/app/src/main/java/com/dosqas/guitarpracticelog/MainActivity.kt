package com.dosqas.guitarpracticelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dosqas.guitarpracticelog.data.db.AppDatabase
import com.dosqas.guitarpracticelog.data.repository.PracticeRepositoryImpl
import com.dosqas.guitarpracticelog.ui.theme.GuitarPracticeLogTheme
import com.dosqas.guitarpracticelog.ui.list.PracticeListScreen
import com.dosqas.guitarpracticelog.viewmodel.PracticeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val migration2to3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE UNIQUE INDEX `index_practice_sessions_songTitle` ON `practice_sessions` (`songTitle`)")
            }
        }

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "practice_db"
        )
        .addMigrations(migration2to3)
        .build()

        val repository = PracticeRepositoryImpl(db.practiceDao())
        val viewModel = PracticeViewModel(repository)
        setContent {
            // Use Material3 theme
            MaterialTheme {
                Surface {
                    PracticeListScreen(viewModel)  // Show your main list screen
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GuitarPracticeLogTheme {
        Greeting("Android")
    }
}
