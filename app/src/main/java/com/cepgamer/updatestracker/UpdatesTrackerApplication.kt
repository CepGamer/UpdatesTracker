package com.cepgamer.updatestracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.cepgamer.updatestracker.background.DownloadWorker
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class UpdatesTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val workRequest = PeriodicWorkRequestBuilder<DownloadWorker>(UPDATE_PERIOD)

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "unique_updater_work",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest.build()
        )

        val mainChannel = NotificationChannel(DownloadWorker.NOTIFICATION_ID.toString(), "WebsiteUpdates", NotificationManager.IMPORTANCE_HIGH)
        getSystemService(NotificationManager::class.java).createNotificationChannel(mainChannel)
    }

    companion object {
        val UPDATE_PERIOD = 1.minutes.toJavaDuration()
    }
}