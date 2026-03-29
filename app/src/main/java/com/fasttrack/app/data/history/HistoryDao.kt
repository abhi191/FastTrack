package com.fasttrack.app.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: FastingSession)

    @Query("SELECT * FROM fasting_history ORDER BY endTimeMillis DESC")
    fun getAllHistory(): Flow<List<FastingSession>>

    @Query("DELETE FROM fasting_history")
    suspend fun clearAll()
}
