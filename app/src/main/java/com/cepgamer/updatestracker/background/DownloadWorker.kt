package com.cepgamer.updatestracker.background

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.cepgamer.updatestracker.MainActivity
import com.cepgamer.updatestracker.model.RawHtmlEntity
import com.cepgamer.updatestracker.model.RawHtmlUpdater

class DownloadWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val updater = RawHtmlUpdater(appContext)

    private val addresses = mutableListOf<String>()

    override suspend fun doWork(): Result {
        val result = updater.updateHtmls()

        Log.i(this::class.simpleName, "Worker iteration")

        if (result.isNotEmpty()) {
            Log.i(this::class.simpleName, "Address update. Showing notification.")
            addresses += result.map(RawHtmlEntity::address)
            if (ActivityCompat.checkSelfPermission(
                    this.appContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i(this::class.simpleName, "Can't show notifications.")
            }

            NotificationManagerCompat.from(applicationContext)
                .notify(NOTIFICATION_ID, createNotification())
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setChannelId(NOTIFICATION_ID.toString())
            .setContentIntent(
                PendingIntent.getActivity(
                    appContext,
                    0,
                    Intent(appContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setSmallIcon(androidx.appcompat.R.drawable.abc_ic_arrow_drop_right_black_24dp)
            .build()
    }

    companion object {
        val ADDRESS_TAG = "address"

        const val NOTIFICATION_ID = 115
    }
}