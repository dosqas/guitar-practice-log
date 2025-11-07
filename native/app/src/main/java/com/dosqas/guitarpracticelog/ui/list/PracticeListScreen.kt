package com.dosqas.guitarpracticelog.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dosqas.guitarpracticelog.data.PracticeSession
import com.dosqas.guitarpracticelog.ui.create.CreateSessionScreen
import com.dosqas.guitarpracticelog.ui.update.UpdateSessionScreen
import com.dosqas.guitarpracticelog.viewmodel.PracticeViewModel

@Composable
fun PracticeListScreen(viewModel: PracticeViewModel = viewModel()) {

    // State for showing create or update screens
    var showCreateScreen by remember { mutableStateOf(false) }
    var sessionToUpdate by remember { mutableStateOf<PracticeSession?>(null) }

    // Observe sessions from ViewModel
    val sessions by viewModel.sessions.observeAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {

        // Main List
        if (!showCreateScreen && sessionToUpdate == null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions) { session ->
                    PracticeCard(
                        session = session,
                        onDelete = { viewModel.deleteSession(it) },
                        onClick = { sessionToUpdate = session } // open update screen
                    )
                }
            }

            // Floating Action Button
            FloatingActionButton(
                onClick = { showCreateScreen = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("+")
            }
        }

        // Show Create Session Screen
        if (showCreateScreen) {
            CreateSessionScreen(
                viewModel = viewModel,
                onSave = { showCreateScreen = false },
                onBack = { showCreateScreen = false }
            )
        }

        // Show Update Session Screen
        sessionToUpdate?.let { session ->
            UpdateSessionScreen(
                session = session,
                viewModel = viewModel,
                onSave = { sessionToUpdate = null },
                onBack = { sessionToUpdate = null }
            )
        }
    }
}
