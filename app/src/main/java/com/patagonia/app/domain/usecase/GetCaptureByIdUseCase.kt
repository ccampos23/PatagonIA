package com.patagonia.app.domain.usecase

import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.repository.CaptureRepository
import javax.inject.Inject

class GetCaptureByIdUseCase @Inject constructor(
    private val repository: CaptureRepository
) {
    suspend operator fun invoke(id: String): Capture? =
        repository.getCaptureById(id)
}
