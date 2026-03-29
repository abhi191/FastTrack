package com.fasttrack.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fasttrack.app.R

object NotificationHelper {
    private const val CHANNEL_ID = "fasting_milestones"
    private const val CHANNEL_NAME = "Fasting Milestones"
    private const val CHANNEL_DESC = "Notifications for reaching key fasting stages"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMilestoneNotification(context: Context, stageName: String, stageEmoji: String, description: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Milestone Reached! $stageEmoji")
            .setContentText("You've entered the $stageName stage!")
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Use a semi-random ID based on the stage name so multiple stages can show at once if missed
        val notificationId = stageName.hashCode()
        notificationManager.notify(notificationId, builder.build())
    }
}
