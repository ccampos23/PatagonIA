package com.patagonia.app.data.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncOrchestrator @Inject constructor(
    private val workManager: WorkManager
) {

    fun enqueueSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15,
                TimeUnit.SECONDS
            )
            .setInputData(workDataOf("is_fresh_install" to false))
            .build()

        workManager.enqueueUniqueWork(
            "capture_sync",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun enqueueFreshInstallSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15,
                TimeUnit.SECONDS
            )
            .setInputData(workDataOf("is_fresh_install" to true))
            .build()

        workManager.enqueueUniqueWork(
            "capture_sync",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun observeSyncState(): Flow<List<WorkInfo>> {
        return workManager.getWorkInfosForUniqueWorkFlow("capture_sync")
    }
}
