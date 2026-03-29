package com.fasttrack.app

import android.app.Application
import com.fasttrack.app.data.history.AppDatabase
import com.fasttrack.app.notifications.NotificationHelper

class FastTrackApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
