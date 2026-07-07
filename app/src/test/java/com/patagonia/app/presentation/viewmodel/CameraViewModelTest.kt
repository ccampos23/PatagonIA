package com.patagonia.app.presentation.viewmodel

import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.domain.repository.LocationResult
import com.patagonia.app.domain.repository.LocationTracker
import com.patagonia.app.domain.usecase.AddCaptureUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [CameraViewModel] GPS-location integration.
 *
 * Validates that:
 *  1. Real coordinates from [LocationTracker] are used when available.
 *  2. Default Patagonia fallback coordinates are used when location is null.
 *  3. Default coordinates are used when the tracker throws an exception.
 *  4. Capture metadata (species, notes, etc.) is forwarded correctly.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var addCaptureUseCase: AddCaptureUseCase
    private lateinit var locationTracker: LocationTracker
    private lateinit var viewModel: CameraViewModel

    companion object {
        private const val DEFAULT_LATITUDE = -45.57
        private const val DEFAULT_LONGITUDE = -72.06
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        addCaptureUseCase = mock()
        locationTracker = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── 1. Real location ────────────────────────────────────────────────

    @Test
    fun `saveCapture uses real coordinates when location tracker returns a result`() = runTest {
        // Arrange
        val realLocation = LocationResult(latitude = -50.123, longitude = -73.456)
        whenever(locationTracker.getCurrentLocation()).thenReturn(realLocation)
        viewModel = CameraViewModel(addCaptureUseCase, locationTracker)

        // Act
        viewModel.saveCapture(
            speciesName = "Puma",
            scientificName = "Puma concolor",
            notes = "Near the glacier",
            imagePath = "/photos/puma.jpg",
            confidence = 0.92f
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val captor = argumentCaptor<Capture>()
        verify(addCaptureUseCase).invoke(captor.capture())
        val saved = captor.firstValue
        assertEquals(-50.123, saved.latitude, 0.0001)
        assertEquals(-73.456, saved.longitude, 0.0001)
    }

    // ── 2. Null location → fallback ─────────────────────────────────────

    @Test
    fun `saveCapture falls back to default coordinates when location tracker returns null`() = runTest {
        // Arrange
        whenever(locationTracker.getCurrentLocation()).thenReturn(null)
        viewModel = CameraViewModel(addCaptureUseCase, locationTracker)

        // Act
        viewModel.saveCapture(
            speciesName = "Condor",
            scientificName = "Vultur gryphus",
            notes = null,
            imagePath = "/photos/condor.jpg"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val captor = argumentCaptor<Capture>()
        verify(addCaptureUseCase).invoke(captor.capture())
        val saved = captor.firstValue
        assertEquals(DEFAULT_LATITUDE, saved.latitude, 0.0001)
        assertEquals(DEFAULT_LONGITUDE, saved.longitude, 0.0001)
    }

    // ── 3. Exception → fallback ─────────────────────────────────────────

    @Test
    fun `saveCapture falls back to default coordinates when location tracker throws`() = runTest {
        // Arrange
        whenever(locationTracker.getCurrentLocation()).thenThrow(SecurityException("Permission denied"))
        viewModel = CameraViewModel(addCaptureUseCase, locationTracker)

        // Act
        viewModel.saveCapture(
            speciesName = "Huemul",
            scientificName = "Hippocamelus bisulcus",
            notes = "Spotted in forest",
            imagePath = "/photos/huemul.jpg",
            confidence = 0.85f
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val captor = argumentCaptor<Capture>()
        verify(addCaptureUseCase).invoke(captor.capture())
        val saved = captor.firstValue
        assertEquals(DEFAULT_LATITUDE, saved.latitude, 0.0001)
        assertEquals(DEFAULT_LONGITUDE, saved.longitude, 0.0001)
    }

    // ── 4. Capture metadata forwarded correctly ─────────────────────────

    @Test
    fun `saveCapture forwards species data and notes correctly`() = runTest {
        // Arrange
        val realLocation = LocationResult(latitude = -41.0, longitude = -71.8)
        whenever(locationTracker.getCurrentLocation()).thenReturn(realLocation)
        viewModel = CameraViewModel(addCaptureUseCase, locationTracker)

        // Act
        viewModel.saveCapture(
            speciesName = "  Pudú  ",
            scientificName = "  Pudu puda  ",
            notes = "  Tiny deer  ",
            imagePath = "/photos/pudu.jpg",
            confidence = 0.78f
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val captor = argumentCaptor<Capture>()
        verify(addCaptureUseCase).invoke(captor.capture())
        val saved = captor.firstValue
        assertEquals("Pudú", saved.speciesName)
        assertEquals("Pudu puda", saved.scientificName)
        assertEquals("Tiny deer", saved.notes)
        assertEquals("/photos/pudu.jpg", saved.imagePath)
        assertEquals(0.78f, saved.confidence)
        assertEquals(SyncStatus.PENDING_INSERT, saved.syncStatus)
    }

    // ── 5. Recognition state management ─────────────────────────────────

    @Test
    fun `updateRecognitions updates the recognitions state`() = runTest {
        // Arrange
        whenever(locationTracker.getCurrentLocation()).thenReturn(null)
        viewModel = CameraViewModel(addCaptureUseCase, locationTracker)

        val recognitions = listOf(
            com.patagonia.app.domain.model.Recognition("Puma", 0.95f),
            com.patagonia.app.domain.model.Recognition("Huemul", 0.72f)
        )

        // Act
        viewModel.updateRecognitions(recognitions)

        // Assert
        assertEquals(2, viewModel.recognitions.value.size)
        assertEquals("Puma", viewModel.recognitions.value[0].title)
    }

    // ── 6. Default confidence is null when omitted ──────────────────────

    @Test
    fun `saveCapture defaults confidence to null when not provided`() = runTest {
        // Arrange
        whenever(locationTracker.getCurrentLocation()).thenReturn(null)
        viewModel = CameraViewModel(addCaptureUseCase, locationTracker)

        // Act
        viewModel.saveCapture(
            speciesName = "Monito del monte",
            scientificName = null,
            notes = null,
            imagePath = "/photos/monito.jpg"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val captor = argumentCaptor<Capture>()
        verify(addCaptureUseCase).invoke(captor.capture())
        val saved = captor.firstValue
        assertEquals(null, saved.confidence)
        assertEquals("Species indet.", saved.scientificName)
    }
}
