package com.sundaytools.app.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ArchiveScheduler {

    /**
     * (Re)schedules the daily archive job so its first run lands at [hour]:[minute]
     * local time (default 11:00 PM per the spec), then repeats every 24 hours.
     * Call this on app start and whenever the user changes Archive Time in Settings.
     */
    fun schedule(context: Context, hour: Int = 23, minute: Int = 0) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val initialDelayMillis = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<ArchiveWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ArchiveWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
