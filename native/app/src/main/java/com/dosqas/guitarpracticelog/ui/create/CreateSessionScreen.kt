package com.dosqas.guitarpracticelog.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dosqas.guitarpracticelog.viewmodel.PracticeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionScreen(
    viewModel: PracticeViewModel = viewModel(),
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    // Input field states
    var songTitle by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var duration by remember { mutableStateOf("") }
    var focusArea by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Error states
    var songTitleError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }
    var dateInvalid by remember { mutableStateOf(false) }
    var durationError by remember { mutableStateOf(false) }
    var durationInvalid by remember { mutableStateOf(false)}
    var focusAreaError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Session") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Validation
                        songTitleError = songTitle.isBlank()
                        dateError = date.isBlank()
                        durationError = duration.isBlank()
                        focusAreaError = focusArea.isBlank()

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        var parsedDate = LocalDate.now()
                        try {
                            parsedDate = LocalDate.parse(date, formatter)
                        } catch (_: Exception) {
                            dateInvalid = true
                        }

                        val durationInt = duration.toIntOrNull()
                        durationInvalid = durationInt == null && duration.isNotBlank()

                        if (!songTitleError && !dateError && !dateInvalid && !durationError && !focusAreaError && !durationInvalid) {
                            viewModel.addSession(
                                songTitle = songTitle,
                                date = parsedDate,
                                duration = durationInt!!,
                                focus = focusArea,
                                notes = notes
                            )
                            onSave()
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = songTitle,
                onValueChange = {
                    songTitle = it
                    if (it.isNotBlank()) songTitleError = false
                },
                label = { Text("Song Title") },
                isError = songTitleError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (songTitleError) {
                Text("This field is required", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = date,
                onValueChange = {
                    date = it

                    dateError = false
                    dateInvalid = false
                },
                label = { Text("Date (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (dateError) {
                Text("This field is required", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            } else if (dateInvalid) {
                Text("Please enter a valid date", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = duration,
                onValueChange = {
                    duration = it

                    durationError = false
                    durationInvalid = false
                },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = durationError || durationInvalid,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (durationError) {
                Text("This field is required", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            } else if (durationInvalid) {
                Text("Please enter a valid number", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = focusArea,
                onValueChange = {
                    focusArea = it
                    if (it.isNotBlank()) focusAreaError = false
                },
                label = { Text("Focus Area") },
                isError = focusAreaError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (focusAreaError) {
                Text("This field is required", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
