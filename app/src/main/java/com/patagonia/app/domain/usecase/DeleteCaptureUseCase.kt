package com.patagonia.app.domain.usecase

import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.repository.CaptureRepository
import javax.inject.Inject

class DeleteCaptureUseCase @Inject constructor(
    private val repository: CaptureRepository
) {
    suspend operator fun invoke(capture: Capture) {
        repository.deleteCapture(capture)
    }
}
