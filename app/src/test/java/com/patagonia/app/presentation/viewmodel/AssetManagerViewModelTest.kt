package com.patagonia.app.presentation.viewmodel

import com.patagonia.app.domain.model.DownloadedRegion
import com.patagonia.app.domain.model.ParkRegionPreset
import com.patagonia.app.domain.model.TileDownloadState
import com.patagonia.app.domain.repository.TileDownloadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AssetManagerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TileDownloadRepository
    private lateinit var viewModel: AssetManagerViewModel
    private val downloadStateFlow = MutableStateFlow<TileDownloadState>(TileDownloadState.Idle)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        whenever(repository.downloadState).thenReturn(downloadStateFlow)
        runTest(testDispatcher) {
            whenever(repository.getDownloadedRegions()).thenReturn(emptyList())
        }
        viewModel = AssetManagerViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state.downloadedRegions.isEmpty())
        assertEquals(TileDownloadState.Idle, state.downloadState)
        assertEquals("", state.customRegionName)
        assertEquals("", state.west)
        assertEquals("", state.south)
        assertEquals("", state.east)
        assertEquals("", state.north)
        assertNull(state.validationWarning)
        assertNull(state.validationError)
    }

    @Test
    fun `viewModel loads downloaded regions on init`() = runTest {
        val mockRegions = listOf(
            DownloadedRegion("test-id", "Torres del Paine", 10 * 1024 * 1024, 100, 100, true)
        )
        whenever(repository.getDownloadedRegions()).thenReturn(mockRegions)
        
        // Recreate viewModel to run init logic with mock regions
        viewModel = AssetManagerViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.downloadedRegions.size)
        assertEquals("Torres del Paine", state.downloadedRegions[0].name)
    }

    @Test
    fun `downloadState updates in viewModel state when repository flow emits`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(TileDownloadState.Idle, viewModel.uiState.value.downloadState)

        val downloadingState = TileDownloadState.Downloading(
            regionId = "test-id",
            regionName = "Torres del Paine",
            completedResources = 50,
            requiredResources = 100,
            completedBytes = 5 * 1024 * 1024,
            erroredResources = 0
        )
        downloadStateFlow.value = downloadingState
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(downloadingState, viewModel.uiState.value.downloadState)
    }

    @Test
    fun `startPresetDownload invokes repository startDownload`() = runTest {
        val preset = ParkRegionPreset.CHILEAN_PARKS.first() // Torres del Paine (75MB)
        viewModel.startPresetDownload(preset)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository).startDownload(
            regionId = eq(preset.id),
            regionName = eq(preset.nameEs),
            west = eq(preset.west),
            south = eq(preset.south),
            east = eq(preset.east),
            north = eq(preset.north),
            minZoom = eq(preset.minZoom),
            maxZoom = eq(preset.maxZoom)
        )
    }

    @Test
    fun `inputting custom bounding box values works`() = runTest {
        viewModel.updateCustomRegionName("My Area")
        viewModel.updateCoordinates(west = "-73.0", south = "-51.0", east = "-72.0", north = "-50.0")
        
        val state = viewModel.uiState.value
        assertEquals("My Area", state.customRegionName)
        assertEquals("-73.0", state.west)
        assertEquals("-51.0", state.south)
        assertEquals("-72.0", state.east)
        assertEquals("-50.0", state.north)
    }

    @Test
    fun `custom bounding box size warning appears if size exceeds 100MB`() = runTest {
        // Mock size estimation to return 120MB (exceeds 100MB threshold)
        whenever(repository.estimateDownloadSizeMB(any(), any(), any(), any(), any(), any()))
            .thenReturn(120)

        viewModel.updateCoordinates(west = "-73.0", south = "-51.0", east = "-72.0", north = "-50.0")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.validationWarning != null)
        assertTrue(state.validationWarning!!.contains("excede los 100MB"))
        assertNull(state.validationError)
    }

    @Test
    fun `custom bounding box size block error appears if size exceeds hard limit of 750k tiles`() = runTest {
        // Let's assume we estimate tile count or size that exceeds limit.
        // If we simulate that by return value of size or an explicit tiles calculation.
        // Let's implement estimate size that is extremely high (e.g. 500MB) which we block or we block based on estimate size exceeding tiles limit.
        // Wait, D-18 says: "Warn if size exceeds 100MB, and hard block if it exceeds Mapbox's tile store limits (750k tiles)"
        // Let's simulate a huge size or tile count. Let's say if estimated size is > 500MB, we trigger the block.
        whenever(repository.estimateDownloadSizeMB(any(), any(), any(), any(), any(), any()))
            .thenReturn(800) // Huge estimate

        viewModel.updateCoordinates(west = "-73.0", south = "-51.0", east = "-72.0", north = "-50.0")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.validationError != null)
        assertTrue(state.validationError!!.contains("límite máximo"))
    }

    @Test
    fun `deleteRegion invokes repository deleteRegion and reloads list`() = runTest {
        viewModel.deleteRegion("region-to-delete")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository).deleteRegion("region-to-delete")
        verify(repository, times(2)).getDownloadedRegions()
    }
}
