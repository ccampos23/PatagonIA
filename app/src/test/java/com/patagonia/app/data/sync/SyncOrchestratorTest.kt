package com.patagonia.app.data.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.concurrent.TimeUnit

class SyncOrchestratorTest {

    private lateinit var workManager: WorkManager
    private lateinit var orchestrator: SyncOrchestrator

    @Before
    fun setup() {
        workManager = mock()
        orchestrator = SyncOrchestrator(workManager)
    }

    @Test
    fun `enqueueSync enqueues unique work with CONNECTED constraint and backoff`() {
        orchestrator.enqueueSync()

        val captor = argumentCaptor<OneTimeWorkRequest>()
        verify(workManager).enqueueUniqueWork(
            eq("capture_sync"),
            eq(ExistingWorkPolicy.KEEP),
            captor.capture()
        )

        val request = captor.firstValue
        val constraints = request.workSpec.constraints

        assertEquals(NetworkType.CONNECTED, constraints.requiredNetworkType)
        assertFalse(constraints.requiresCharging())

        assertEquals(BackoffPolicy.EXPONENTIAL, request.workSpec.backoffPolicy)
        // Backoff starts at 15s, doubles up to 120s max (D-08)
        assertEquals(15000L, request.workSpec.backoffDelayDuration) // backoff delay in ms
        assertEquals(false, request.workSpec.input.getBoolean("is_fresh_install", true))
    }

    @Test
    fun `enqueueFreshInstallSync enqueues unique work with REPLACE policy and fresh install flag`() {
        orchestrator.enqueueFreshInstallSync()

        val captor = argumentCaptor<OneTimeWorkRequest>()
        verify(workManager).enqueueUniqueWork(
            eq("capture_sync"),
            eq(ExistingWorkPolicy.REPLACE),
            captor.capture()
        )

        val request = captor.firstValue
        assertEquals(true, request.workSpec.input.getBoolean("is_fresh_install", false))
    }
}
