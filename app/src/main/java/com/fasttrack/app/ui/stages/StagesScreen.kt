package com.fasttrack.app.ui.stages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fasttrack.app.theme.StageCompleted
import com.fasttrack.app.ui.timer.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StagesScreen(viewModel: TimerViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentStage by viewModel.currentStage.collectAsStateWithLifecycle()
    val elapsedHours = state.elapsedMillis / 1000f / 3600f
    val isActive = state.isRunning || state.elapsedMillis > 0

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Body Stages", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        if (isActive && currentStage != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = currentStage?.emoji ?: "", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Current Stage", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        Text(currentStage?.name ?: "", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            itemsIndexed(fastingStages) { index, stage ->
                val isCompleted = isActive && elapsedHours >= stage.thresholdHours &&
                    (index < fastingStages.lastIndex && elapsedHours >= fastingStages[index + 1].thresholdHours)
                val isCurrent = currentStage?.id == stage.id && isActive
                val isPending = !isActive || elapsedHours < stage.thresholdHours

                StageTimelineItem(stage, isCompleted, isCurrent, isPending, index == fastingStages.lastIndex)
            }
        }
    }
}

@Composable
fun StageTimelineItem(stage: FastingStage, isCompleted: Boolean, isCurrent: Boolean, isPending: Boolean, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(48.dp)) {
            Box(
                modifier = Modifier
                    .size(if (isCurrent) 32.dp else 24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isCompleted -> StageCompleted
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, "Completed", tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(14.dp))
                } else if (isCurrent) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onPrimary))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(if (isCompleted || isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        // Stage card
        Card(
            modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
            ),
            border = if (isCurrent) CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = SolidColor(MaterialTheme.colorScheme.primary)) else null
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stage.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            stage.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isPending && !isCurrent) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(stage.timeRange, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stage.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPending && !isCurrent) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
