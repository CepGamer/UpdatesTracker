package com.cepgamer.updatestracker

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.cepgamer.updatestracker.background.DownloadWorker
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class UpdatesTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val workRequest = PeriodicWorkRequestBuilder<DownloadWorker>(30.minutes.toJavaDuration())

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "unique_updater_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest.build()
        )
    }
}