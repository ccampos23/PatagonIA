package com.patagonia.app.data.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(RobolectricTestRunner::class)
class SyncWorkerTest {

    private lateinit var context: Context
    private lateinit var syncEngine: CaptureSyncEngine

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        syncEngine = mock()
    }

    private fun createWorker(isFreshInstall: Boolean = false, runAttemptCount: Int = 0): SyncWorker {
        val factory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker? {
                // Mock runAttemptCount on the parameters if needed, or we can use custom parameters builder
                // TestListenableWorkerBuilder handles the parameters. We can mock parameters or set attempt count.
                return SyncWorker(appContext, workerParameters, syncEngine)
            }
        }
        
        return TestListenableWorkerBuilder.from(context, SyncWorker::class.java)
            .setWorkerFactory(factory)
            .setInputData(workDataOf("is_fresh_install" to isFreshInstall))
            .setRunAttemptCount(runAttemptCount)
            .build()
    }

    @Test
    fun `SyncWorker returns success on full sync`() = runTest {
        whenever(syncEngine.pushCaptures()).thenReturn(SyncResult.Success(1, 0))
        whenever(syncEngine.pullCaptures(false)).thenReturn(SyncResult.Success(0, 2))

        val worker = createWorker(isFreshInstall = false)
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)
    }

    @Test
    fun `SyncWorker returns retry on Failure when runAttemptCount less than 3`() = runTest {
        whenever(syncEngine.pushCaptures()).thenReturn(SyncResult.Failure(RuntimeException("Transient network error")))

        val worker = createWorker(isFreshInstall = false, runAttemptCount = 1)
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Retry)
    }

    @Test
    fun `SyncWorker returns failure on Failure when runAttemptCount is 3 or more`() = runTest {
        whenever(syncEngine.pushCaptures()).thenReturn(SyncResult.Failure(RuntimeException("Fatal connection error")))

        val worker = createWorker(isFreshInstall = false, runAttemptCount = 3)
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Failure)
    }

    @Test
    fun `SyncWorker returns success on PartialSuccess`() = runTest {
        // Healthy data enqueued, failed quarantined -> return Success (D-22)
        val errors = listOf(SyncError("c-1", "Image corrupt", RuntimeException("Corrupt")))
        whenever(syncEngine.pushCaptures()).thenReturn(SyncResult.PartialSuccess(1, 0, 1, errors))
        whenever(syncEngine.pullCaptures(false)).thenReturn(SyncResult.Success(0, 0))

        val worker = createWorker(isFreshInstall = false)
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)
    }
}
