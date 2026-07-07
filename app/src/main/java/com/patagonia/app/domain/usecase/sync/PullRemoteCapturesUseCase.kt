package com.patagonia.app.domain.usecase.sync

import com.patagonia.app.data.sync.SyncOrchestrator
import javax.inject.Inject

class PullRemoteCapturesUseCase @Inject constructor(
    private val orchestrator: SyncOrchestrator
) {
    operator fun invoke() {
        orchestrator.enqueueFreshInstallSync()
    }
}
