package com.fasttrack.app.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fasting_history")
data class FastingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val targetDurationHours: Int,
    val successful: Boolean,
    val startingWeight: Float? = null // Optional starting weight
)
