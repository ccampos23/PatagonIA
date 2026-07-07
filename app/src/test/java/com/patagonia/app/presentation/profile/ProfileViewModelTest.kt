package com.patagonia.app.presentation.profile

import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthRepository
import com.patagonia.app.domain.repository.CaptureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var captureRepository: CaptureRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        captureRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel loads user profile and capture details`() = runTest {
        val user = UserProfile(
            id = "user-123",
            email = "user@test.com",
            username = "patagonian_explorer",
            bio = "Loving the wilderness",
            level = 5,
            avatarUrl = "https://avatar.url"
        )
        whenever(authRepository.observeSession()).thenReturn(flowOf(user))
        
        val captures = listOf(
            Capture(
                id = "c-1", speciesName = "Puma", scientificName = "", timestamp = 0L, imagePath = "",
                latitude = 0.0, longitude = 0.0, altitude = null, confidence = null, notes = null,
                syncStatus = SyncStatus.SYNCED
            ),
            Capture(
                id = "c-2", speciesName = "Huemul", scientificName = "", timestamp = 0L, imagePath = "",
                latitude = 0.0, longitude = 0.0, altitude = null, confidence = null, notes = null,
                syncStatus = SyncStatus.SYNC_FAILED
            )
        )
        whenever(captureRepository.getCaptures()).thenReturn(flowOf(captures))
        whenever(captureRepository.getPendingSyncCaptures()).thenReturn(flowOf(listOf(captures[1])))

        viewModel = ProfileViewModel(authRepository, captureRepository)
        
        // Collect Flow in background scope to trigger WhileSubscribed StateFlow
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            viewModel.uiState.collect {}
        }

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("patagonian_explorer", state.username)
        assertEquals("Loving the wilderness", state.bio)
        assertEquals(5, state.level)
        assertEquals("https://avatar.url", state.avatarUrl)
        assertEquals(2, state.captureCount)
        assertEquals(1, state.syncErrorCount)
        assertTrue(state.showSyncWarning)
    }
}
