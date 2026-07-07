package com.patagonia.app.domain.usecase

import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.domain.repository.CaptureRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DeleteCaptureUseCaseTest {

    private lateinit var repository: CaptureRepository
    private lateinit var useCase: DeleteCaptureUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = DeleteCaptureUseCase(repository)
    }

    @Test
    fun `invoke with string ID calls repository deleteCapture`() = runTest {
        useCase("test-1")

        verify(repository).deleteCapture("test-1")
    }

    @Test
    fun `invoke with Capture calls repository deleteCapture using ID`() = runTest {
        val capture = Capture(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "/img",
            latitude = -50.0,
            longitude = -73.0,
            altitude = null,
            confidence = 0.9f,
            notes = "",
            syncStatus = SyncStatus.PENDING_INSERT
        )

        useCase(capture)

        verify(repository).deleteCapture("test-1")
    }
}
