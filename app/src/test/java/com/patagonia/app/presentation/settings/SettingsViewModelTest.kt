package com.patagonia.app.presentation.settings

import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthRepository
import com.patagonia.app.domain.repository.CaptureRepository
import com.patagonia.app.domain.usecase.profile.UpdateProfileUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var updateProfileUseCase: UpdateProfileUseCase
    private lateinit var captureRepository: CaptureRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        updateProfileUseCase = mock()
        captureRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel loads user settings and sync errors`() = runTest {
        val user = UserProfile(
            id = "user-123",
            email = "user@test.com",
            username = "patagonian_explorer",
            isPrivate = true
        )
        whenever(authRepository.observeSession()).thenReturn(flowOf(user))
        
        val captures = listOf(
            Capture(
                id = "c-1", speciesName = "Puma", scientificName = "", timestamp = 0L, imagePath = "",
                latitude = 0.0, longitude = 0.0, altitude = null, confidence = null, notes = null,
                syncStatus = SyncStatus.SYNC_FAILED
            )
        )
        whenever(captureRepository.getPendingSyncCaptures()).thenReturn(flowOf(captures))

        viewModel = SettingsViewModel(authRepository, updateProfileUseCase, captureRepository)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("user@test.com", state.email)
        assertEquals("patagonian_explorer", state.username)
        assertTrue(state.isPrivate)
        assertTrue(state.hasSyncErrors)
    }

    @Test
    fun `togglePrivacy calls updateProfileUseCase and updates state`() = runTest {
        val user = UserProfile(
            id = "user-123",
            email = "user@test.com",
            username = "patagonian_explorer",
            bio = "Old bio",
            isPrivate = true
        )
        whenever(authRepository.observeSession()).thenReturn(flowOf(user))
        whenever(captureRepository.getPendingSyncCaptures()).thenReturn(flowOf(emptyList()))
        val updatedUser = user.copy(isPrivate = false)
        whenever(updateProfileUseCase(any(), any())).thenReturn(Result.success(updatedUser))

        viewModel = SettingsViewModel(authRepository, updateProfileUseCase, captureRepository)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.togglePrivacy(false)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(updateProfileUseCase).invoke("Old bio", false)
    }

    @Test
    fun `logout triggers authRepository logout`() = runTest {
        val user = UserProfile(
            id = "user-123",
            email = "user@test.com",
            username = "patagonian_explorer",
            isPrivate = true
        )
        whenever(authRepository.observeSession()).thenReturn(flowOf(user))
        whenever(captureRepository.getPendingSyncCaptures()).thenReturn(flowOf(emptyList()))
        whenever(authRepository.logout()).thenReturn(Result.success(Unit))

        viewModel = SettingsViewModel(authRepository, updateProfileUseCase, captureRepository)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(authRepository).logout()
    }

    @Test
    fun `retrySync resets status of SYNC_FAILED captures`() = runTest {
        val user = UserProfile(
            id = "user-123",
            email = "user@test.com",
            username = "patagonian_explorer",
            isPrivate = true
        )
        whenever(authRepository.observeSession()).thenReturn(flowOf(user))
        
        val failedCapture = Capture(
            id = "c-1", speciesName = "Puma", scientificName = "", timestamp = 0L, imagePath = "",
            latitude = 0.0, longitude = 0.0, altitude = null, confidence = null, notes = null,
            syncStatus = SyncStatus.SYNC_FAILED
        )
        whenever(captureRepository.getPendingSyncCaptures()).thenReturn(flowOf(listOf(failedCapture)))

        viewModel = SettingsViewModel(authRepository, updateProfileUseCase, captureRepository)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.retrySync()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify that retrySync updates the capture to PENDING_INSERT (or updates it in the repository)
        verify(captureRepository).updateCapture(any())
    }
}
