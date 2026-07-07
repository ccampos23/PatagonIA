package com.patagonia.app.domain.usecase.sync

import com.patagonia.app.data.sync.SyncOrchestrator
import javax.inject.Inject

class EnqueueSyncUseCase @Inject constructor(
    private val orchestrator: SyncOrchestrator
) {
    operator fun invoke() {
        orchestrator.enqueueSync()
    }
}
