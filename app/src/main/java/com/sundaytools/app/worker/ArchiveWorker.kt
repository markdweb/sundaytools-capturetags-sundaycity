package com.sundaytools.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sundaytools.app.repository.PlayerTagRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Runs once a day at the configured Archive Time. Because rows are already
 * grouped by [com.sundaytools.app.database.entity.PlayerTagEntity.dayKey],
 * "moving today's records into History" happens implicitly at midnight local
 * time -- yesterday's dayKey simply stops matching `DateUtils.todayKey()` and
 * the UI shows it under History automatically. This worker exists as the hook
 * point for future archive-time behavior (e.g. a "day closed" notification,
 * automatic backup, or pruning), configurable via the Settings screen.
 */
@HiltWorker
class ArchiveWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: PlayerTagRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Touches the repository so any future archive-time side effects
            // (notifications, backups) have a natural place to live.
            repository.getAllTags()
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sundaytools_daily_archive"
    }
}
