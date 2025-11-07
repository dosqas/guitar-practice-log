package com.dosqas.guitarpracticelog.ui.list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dosqas.guitarpracticelog.data.PracticeSession

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun PracticeCard(
    session: PracticeSession,
    onDelete: (Int) -> Unit,
    onClick: () -> Unit
) {
    // State to show/hide the confirmation dialog
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Song: ${session.songTitle}", style = MaterialTheme.typography.titleMedium)
            Text("Date: ${session.date}", style = MaterialTheme.typography.bodyMedium)
            Text("Duration: ${session.durationMinutes} min", style = MaterialTheme.typography.bodyMedium)
            Text("Focus: ${session.focusArea}", style = MaterialTheme.typography.bodyMedium)
            session.notes?.let {
                if (it.isNotBlank()) Text("Notes: $it", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delete Button
            Button(
                onClick = { showDialog = true }, // Show confirmation dialog
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this session?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(session.id)  // Call delete
                        showDialog = false    // Close dialog
                    }
                ) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}
