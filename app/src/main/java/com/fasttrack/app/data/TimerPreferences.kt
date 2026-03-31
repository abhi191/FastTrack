package com.fasttrack.app.data

import android.content.Context
import android.content.SharedPreferences
import com.fasttrack.app.ui.timer.TimerState

class TimerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("timer_prefs", Context.MODE_PRIVATE)

    fun getTimerState(): TimerState {
        return TimerState(
            isRunning = prefs.getBoolean("is_running", false),
            startTimeMillis = prefs.getLong("start_time", 0L),
            targetDurationHours = prefs.getInt("target_duration", 16),
            startingWeight = prefs.getString("weight", "") ?: "",
            isWeightInLbs = prefs.getBoolean("is_lbs", false),
            elapsedMillis = 0L // Re-calculated in ViewModel
        )
    }

    fun saveTimerState(state: TimerState) {
        prefs.edit()
            .putBoolean("is_running", state.isRunning)
            .putLong("start_time", state.startTimeMillis)
            .putInt("target_duration", state.targetDurationHours)
            .putString("weight", state.startingWeight)
            .putBoolean("is_lbs", state.isWeightInLbs)
            .commit() // Synchronous save guarantees it works before app is swiped away
    }
}
