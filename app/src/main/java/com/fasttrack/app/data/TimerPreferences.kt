package com.fasttrack.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fasttrack.app.ui.timer.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object TimerPreferences {
    private val IS_RUNNING_KEY = booleanPreferencesKey("timer_is_running")
    private val START_TIME_MILLIS_KEY = longPreferencesKey("timer_start_time")
    private val TARGET_DURATION_KEY = intPreferencesKey("timer_target_duration")
    private val STARTING_WEIGHT_KEY = stringPreferencesKey("timer_starting_weight")
    private val IS_WEIGHT_LBS_KEY = booleanPreferencesKey("timer_is_weight_lbs")

    fun getTimerState(context: Context): Flow<TimerState> {
        return context.dataStore.data.map { preferences ->
            TimerState(
                isRunning = preferences[IS_RUNNING_KEY] ?: false,
                startTimeMillis = preferences[START_TIME_MILLIS_KEY] ?: 0L,
                targetDurationHours = preferences[TARGET_DURATION_KEY] ?: 16,
                startingWeight = preferences[STARTING_WEIGHT_KEY] ?: "",
                isWeightInLbs = preferences[IS_WEIGHT_LBS_KEY] ?: false,
                elapsedMillis = 0L // Recalculated locally in ViewModel
            )
        }
    }

    suspend fun saveTimerState(context: Context, state: TimerState) {
        context.dataStore.edit { preferences ->
            preferences[IS_RUNNING_KEY] = state.isRunning
            preferences[START_TIME_MILLIS_KEY] = state.startTimeMillis
            preferences[TARGET_DURATION_KEY] = state.targetDurationHours
            preferences[STARTING_WEIGHT_KEY] = state.startingWeight
            preferences[IS_WEIGHT_LBS_KEY] = state.isWeightInLbs
        }
    }
}
