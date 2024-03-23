package com.cepgamer.updatestracker.background

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.cepgamer.updatestracker.model.RawHtmlEntity
import com.cepgamer.updatestracker.model.RawHtmlUpdater

class DownloadWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val updater = RawHtmlUpdater(appContext)

    private val addresses = mutableListOf<String>()

    override suspend fun doWork(): Result {
        val result = updater.updateHtmls()

        if (result.isNotEmpty()) {
            addresses += result.map(RawHtmlEntity::address)
            Log.i(javaClass.name, "Adding notification.")
            setForeground(getForegroundInfo())
        }

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(appContext, NOTIFICATION_ID.toString())
            .setContentTitle("Update to the list of addresses")
            .setContentText("Addresses updated: ${addresses.joinToString(", ")}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(androidx.appcompat.R.drawable.abc_ic_arrow_drop_right_black_24dp)
            .build()
    }

    companion object {
        val ADDRESS_TAG = "address"

        val NOTIFICATION_ID = 115
    }
}