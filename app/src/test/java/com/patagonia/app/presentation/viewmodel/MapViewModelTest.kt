package com.patagonia.app.presentation.viewmodel

import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.usecase.GetCapturesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCapturesUseCase: GetCapturesUseCase
    private lateinit var viewModel: MapViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCapturesUseCase = mock()
        whenever(getCapturesUseCase.invoke()).thenReturn(flowOf(emptyList()))
        viewModel = MapViewModel(getCapturesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state uses Outdoors style URI`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("mapbox://styles/mapbox/outdoors-v12", state.styleUri)
    }

    @Test
    fun `initial camera centers on Chile`() = runTest {
        val state = viewModel.uiState.value
        // Chile is roughly at -71.5 longitude, -35.0 latitude
        assertEquals(-71.5, state.cameraLongitude, 5.0)
        assertEquals(-35.0, state.cameraLatitude, 5.0)
    }

    @Test
    fun `initial state shows My Journal mode`() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state.showMyJournal)
    }

    @Test
    fun `toggleJournalMode switches from My Journal to Global`() = runTest {
        viewModel.toggleJournalMode()
        val state = viewModel.uiState.value
        assertEquals(false, state.showMyJournal)
    }

    @Test
    fun `toggleJournalMode switches back to My Journal`() = runTest {
        viewModel.toggleJournalMode() // to Global
        viewModel.toggleJournalMode() // back to My Journal
        val state = viewModel.uiState.value
        assertTrue(state.showMyJournal)
    }

    @Test
    fun `initial compass is North-up`() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state.isNorthUp)
    }

    @Test
    fun `toggleCompassOrientation switches to Heading-up`() = runTest {
        viewModel.toggleCompassOrientation()
        val state = viewModel.uiState.value
        assertEquals(false, state.isNorthUp)
    }

    @Test
    fun `captures from use case are reflected in state`() = runTest {
        val captures = listOf(
            Capture(
                id = "1",
                speciesName = "Puma",
                scientificName = "Puma concolor",
                timestamp = System.currentTimeMillis(),
                imagePath = "/test/puma.jpg",
                latitude = -51.0,
                longitude = -73.0,
                altitude = 500.0,
                confidence = 0.95f,
                notes = null,
                isSynced = false
            )
        )
        whenever(getCapturesUseCase.invoke()).thenReturn(flowOf(captures))
        // Recreate ViewModel to pick up new mock behavior
        viewModel = MapViewModel(getCapturesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.captures.size)
        assertEquals("Puma", state.captures[0].speciesName)
    }

    @Test
    fun `initial state has no selected capture`() = runTest {
        val state = viewModel.uiState.value
        assertEquals(null, state.selectedCapture)
        assertEquals(false, state.isTooltipVisible)
    }

    @Test
    fun `selectCapture sets selectedCapture and tooltip visible`() = runTest {
        val capture = Capture(
            id = "1",
            speciesName = "Huemul",
            scientificName = "Hippocamelus bisulcus",
            timestamp = System.currentTimeMillis(),
            imagePath = "/test/huemul.jpg",
            latitude = -48.0,
            longitude = -72.5,
            altitude = 800.0,
            confidence = 0.88f,
            notes = null,
            isSynced = false
        )

        viewModel.selectCapture(capture)
        val state = viewModel.uiState.value
        assertEquals(capture, state.selectedCapture)
        assertTrue(state.isTooltipVisible)
    }

    @Test
    fun `dismissTooltip clears selectedCapture`() = runTest {
        val capture = Capture(
            id = "2",
            speciesName = "Condor",
            scientificName = "Vultur gryphus",
            timestamp = System.currentTimeMillis(),
            imagePath = "/test/condor.jpg",
            latitude = -33.4,
            longitude = -70.6,
            altitude = 3000.0,
            confidence = 0.95f,
            notes = null,
            isSynced = false
        )

        viewModel.selectCapture(capture)
        assertTrue(viewModel.uiState.value.isTooltipVisible)

        viewModel.dismissTooltip()
        val state = viewModel.uiState.value
        assertEquals(null, state.selectedCapture)
        assertEquals(false, state.isTooltipVisible)
    }
}

