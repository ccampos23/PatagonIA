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
}
