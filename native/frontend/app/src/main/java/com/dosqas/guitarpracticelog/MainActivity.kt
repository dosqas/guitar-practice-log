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
import com.dosqas.guitarpracticelog.data.db.AppDatabase
import com.dosqas.guitarpracticelog.data.remote.RealtimeSyncManager
import com.dosqas.guitarpracticelog.data.remote.RetrofitInstance
import com.dosqas.guitarpracticelog.data.repository.PracticeRepositoryImpl
import com.dosqas.guitarpracticelog.ui.theme.GuitarPracticeLogTheme
import com.dosqas.guitarpracticelog.ui.list.PracticeListScreen
import com.dosqas.guitarpracticelog.viewmodel.PracticeViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    lateinit var realtimeSyncManager: RealtimeSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val dao = db.practiceDao()

        // Dependencies for RealtimeSyncManager
        val client = OkHttpClient.Builder().build()
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, object : JsonDeserializer<LocalDate>,
                JsonSerializer<LocalDate> {
                private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
                    return LocalDate.parse(json.asString, formatter)
                }

                override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
                    return JsonPrimitive(src.format(formatter))
                }
            })
            .create()
        // Instantiate RealtimeSyncManager
        realtimeSyncManager = RealtimeSyncManager(client, dao, gson)

        val repository = PracticeRepositoryImpl(db.practiceDao(),
            RetrofitInstance.api,
            applicationContext
        )
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
