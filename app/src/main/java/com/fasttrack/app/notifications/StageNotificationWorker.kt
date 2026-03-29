package com.fasttrack.app.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class StageNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val stageName = inputData.getString(KEY_STAGE_NAME) ?: return Result.failure()
        val stageEmoji = inputData.getString(KEY_STAGE_EMOJI) ?: ""
        val stageDesc = inputData.getString(KEY_STAGE_DESC) ?: ""

        Log.d("StageNotificationWorker", "Firing notification for $stageName")
        
        NotificationHelper.showMilestoneNotification(
            context = applicationContext,
            stageName = stageName,
            stageEmoji = stageEmoji,
            description = stageDesc
        )

        return Result.success()
    }

    companion object {
        const val KEY_STAGE_NAME = "stage_name"
        const val KEY_STAGE_EMOJI = "stage_emoji"
        const val KEY_STAGE_DESC = "stage_desc"
        const val WORK_TAG = "fasting_milestones"
    }
}
