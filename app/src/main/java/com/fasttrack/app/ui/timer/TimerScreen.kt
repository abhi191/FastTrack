package com.fasttrack.app.ui.timer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fasttrack.app.ui.components.CircularTimer

@Composable
fun TimerScreen(viewModel: TimerViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentStage by viewModel.currentStage.collectAsStateWithLifecycle()
    var showCustomDialog by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    val protocols = listOf("14h" to 14, "16h" to 16, "18h" to 18, "20h" to 20)

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "FastTrack",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        CircularTimer(
            elapsedMillis = state.elapsedMillis,
            targetMillis = state.targetDurationHours * 3600L * 1000L,
            isRunning = state.isRunning,
            currentStageName = currentStage?.name ?: "",
            currentStageEmoji = currentStage?.emoji ?: "",
            isDarkTheme = isDark,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Fasting Protocol", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
        ) {
            protocols.forEach { (label, hours) ->
                FilterChip(
                    selected = state.targetDurationHours == hours,
                    onClick = { viewModel.setTargetHours(hours) },
                    label = { Text(label) },
                    enabled = !state.isRunning,
                )
            }
            FilterChip(
                selected = protocols.none { it.second == state.targetDurationHours },
                onClick = { showCustomDialog = true },
                label = { Text("Custom") },
                enabled = !state.isRunning,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.startingWeight,
            onValueChange = { viewModel.setStartingWeight(it) },
            label = { Text("Starting Weight (Optional)") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = !state.isRunning,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            trailingIcon = { Text("kg / lbs", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(end = 12.dp)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (state.isRunning || state.elapsedMillis > 0) {
                OutlinedButton(onClick = { viewModel.resetTimer() }, modifier = Modifier.height(56.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
            }
            Button(
                onClick = { viewModel.toggleTimer() },
                modifier = Modifier.height(56.dp).weight(1f),
                colors = if (state.isRunning) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
            ) {
                Icon(if (state.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (state.isRunning) "End Fast" else if (state.elapsedMillis > 0) "Resume" else "Start Fast",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showCustomDialog) {
        CustomHoursDialog(state.targetDurationHours, onDismiss = { showCustomDialog = false }) { hours ->
            viewModel.setTargetHours(hours)
            showCustomDialog = false
        }
    }
}

@Composable
fun CustomHoursDialog(currentHours: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var text by remember { mutableStateOf(currentHours.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom Duration") },
        text = {
            Column {
                Text("Enter fasting duration in hours:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = text, onValueChange = { v -> if (v.all { it.isDigit() } && v.length <= 3) text = v }, label = { Text("Hours") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { text.toIntOrNull()?.takeIf { it in 1..168 }?.let(onConfirm) }) { Text("Set") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
