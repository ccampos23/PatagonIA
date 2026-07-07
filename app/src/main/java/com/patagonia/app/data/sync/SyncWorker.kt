package com.patagonia.app.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val captureSyncEngine: CaptureSyncEngine
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val isFreshInstall = inputData.getBoolean("is_fresh_install", false)

        val pushResult = captureSyncEngine.pushCaptures()
        val pullResult = captureSyncEngine.pullCaptures(isFreshInstall)

        return when {
            pushResult is SyncResult.Failure || pullResult is SyncResult.Failure -> {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
            pushResult is SyncResult.PartialSuccess -> {
                // Healthy data enqueued, failed quarantined -> return Success (D-22)
                Result.success()
            }
            else -> {
                Result.success()
            }
        }
    }
}
