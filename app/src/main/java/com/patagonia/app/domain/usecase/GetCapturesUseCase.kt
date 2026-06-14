package com.patagonia.app.domain.usecase

import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCapturesUseCase @Inject constructor(
    private val repository: CaptureRepository
) {
    operator fun invoke(): Flow<List<Capture>> =
        repository.getAllCaptures()
}
