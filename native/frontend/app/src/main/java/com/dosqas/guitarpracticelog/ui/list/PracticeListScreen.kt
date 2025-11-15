package com.dosqas.guitarpracticelog.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dosqas.guitarpracticelog.data.model.PracticeSession
import com.dosqas.guitarpracticelog.data.model.SyncStatus
import com.dosqas.guitarpracticelog.ui.create.CreateSessionScreen
import com.dosqas.guitarpracticelog.ui.update.UpdateSessionScreen
import com.dosqas.guitarpracticelog.viewmodel.PracticeViewModel

@Composable
fun PracticeListScreen(viewModel: PracticeViewModel = viewModel()) {
    var showCreateScreen by remember { mutableStateOf(false) }
    var sessionToUpdate by remember { mutableStateOf<PracticeSession?>(null) }

    val sessions by viewModel.sessionsState

    val errorMessage by viewModel.errorMessage

    val networkError by viewModel.networkStatusFlow.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        networkError?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 8.dp, end = 8.dp)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Box(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
            // Main List
            if (!showCreateScreen && sessionToUpdate == null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sessions.filter { it.status != SyncStatus.WAITING_FOR_DELETE }) { session ->
                        PracticeCard(
                            session = session,
                            onDelete = { viewModel.deleteSession(session.id) },
                            onClick = { sessionToUpdate = session }
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        showCreateScreen = true
                        viewModel.clearError()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text("+")
                }
            }

            if (showCreateScreen) {
                CreateSessionScreen(
                    viewModel = viewModel,
                    onSave = { showCreateScreen = false },
                    onBack = { showCreateScreen = false }
                )
            }

            sessionToUpdate?.let { session ->
                UpdateSessionScreen(
                    session = session,
                    viewModel = viewModel,
                    onSave = { sessionToUpdate = null },
                    onBack = { sessionToUpdate = null }
                )
            }

            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                )
            }
        }
    }
}
