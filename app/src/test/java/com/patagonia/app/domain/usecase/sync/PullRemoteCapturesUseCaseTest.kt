package com.patagonia.app.domain.usecase.sync

import com.patagonia.app.data.sync.SyncOrchestrator
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PullRemoteCapturesUseCaseTest {

    private lateinit var orchestrator: SyncOrchestrator
    private lateinit var useCase: PullRemoteCapturesUseCase

    @Before
    fun setup() {
        orchestrator = mock()
        useCase = PullRemoteCapturesUseCase(orchestrator)
    }

    @Test
    fun `invoke calls orchestrator enqueueFreshInstallSync`() {
        useCase()
        verify(orchestrator).enqueueFreshInstallSync()
    }
}
