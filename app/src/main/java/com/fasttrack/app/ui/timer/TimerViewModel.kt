package com.fasttrack.app.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.fasttrack.app.data.history.FastingSession
import com.fasttrack.app.data.history.HistoryDao
import com.fasttrack.app.notifications.StageNotificationWorker
import com.fasttrack.app.ui.stages.FastingStage
import com.fasttrack.app.ui.stages.fastingStages
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

data class TimerState(
    val isRunning: Boolean = false,
    val elapsedMillis: Long = 0L,
    val targetDurationHours: Int = 16,
    val startTimeMillis: Long = 0L,
    val startingWeight: String = "",
)

class TimerViewModel(
    private val historyDao: HistoryDao,
    private val workManager: WorkManager
) : ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var timerJob: Job? = null

    val currentStage: StateFlow<FastingStage?> = _state.map { s ->
        if (!s.isRunning && s.elapsedMillis == 0L) return@map null
        val elapsedHours = s.elapsedMillis / 1000f / 3600f
        fastingStages.lastOrNull { it.thresholdHours <= elapsedHours }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val targetMillis: Long
        get() = _state.value.targetDurationHours * 3600L * 1000L

    fun setTargetHours(hours: Int) {
        _state.update { it.copy(targetDurationHours = hours) }
    }

    fun setStartingWeight(weightStr: String) {
        _state.update { it.copy(startingWeight = weightStr) }
    }

    fun toggleTimer() {
        if (_state.value.isRunning) endFast() else startTimer()
    }

    fun resetTimer() {
        stopTimer()
        _state.update { it.copy(elapsedMillis = 0L, startTimeMillis = 0L) }
    }

    private fun startTimer() {
        val now = System.currentTimeMillis()
        val previousElapsed = _state.value.elapsedMillis
        _state.update { it.copy(isRunning = true, startTimeMillis = now - previousElapsed) }
        
        scheduleMilestones(now - previousElapsed)

        timerJob = viewModelScope.launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - _state.value.startTimeMillis
                _state.update { it.copy(elapsedMillis = elapsed) }
                delay(1000L)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _state.update { it.copy(isRunning = false) }
        workManager.cancelAllWorkByTag(StageNotificationWorker.WORK_TAG)
    }

    private fun endFast() {
        val currentState = _state.value
        val endTime = System.currentTimeMillis()
        val targetMillis = currentState.targetDurationHours * 3600L * 1000L
        val successful = currentState.elapsedMillis >= targetMillis
        
        val weightFloat = currentState.startingWeight.toFloatOrNull()

        // Save to Room DB
        viewModelScope.launch {
            val session = FastingSession(
                startTimeMillis = currentState.startTimeMillis,
                endTimeMillis = endTime,
                targetDurationHours = currentState.targetDurationHours,
                successful = successful,
                startingWeight = weightFloat
            )
            historyDao.insert(session)
        }

        resetTimer()
    }

    private fun scheduleMilestones(currentElapsedMillis: Long) {
        workManager.cancelAllWorkByTag(StageNotificationWorker.WORK_TAG)

        fastingStages.forEach { stage ->
            val stageMillis = (stage.thresholdHours * 3600L * 1000L).toLong()
            if (stageMillis > currentElapsedMillis && stageMillis > 0) {
                val delayMillis = stageMillis - currentElapsedMillis

                val inputData = Data.Builder()
                    .putString(StageNotificationWorker.KEY_STAGE_NAME, stage.name)
                    .putString(StageNotificationWorker.KEY_STAGE_EMOJI, stage.emoji)
                    .putString(StageNotificationWorker.KEY_STAGE_DESC, stage.description)
                    .build()

                val request = OneTimeWorkRequestBuilder<StageNotificationWorker>()
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(StageNotificationWorker.WORK_TAG)
                    .build()

                workManager.enqueue(request)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

class TimerViewModelFactory(
    private val historyDao: HistoryDao,
    private val workManager: WorkManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(historyDao, workManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
