package com.patagonia.app.domain.usecase.sync

import com.patagonia.app.data.sync.SyncOrchestrator
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class EnqueueSyncUseCaseTest {

    private lateinit var orchestrator: SyncOrchestrator
    private lateinit var useCase: EnqueueSyncUseCase

    @Before
    fun setup() {
        orchestrator = mock()
        useCase = EnqueueSyncUseCase(orchestrator)
    }

    @Test
    fun `invoke calls orchestrator enqueueSync`() {
        useCase()
        verify(orchestrator).enqueueSync()
    }
}
