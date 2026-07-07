package com.patagonia.app.presentation.viewmodel

import app.cash.turbine.test
import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthException
import com.patagonia.app.domain.usecase.auth.GetSessionUseCase
import com.patagonia.app.domain.usecase.auth.LoginUseCase
import com.patagonia.app.domain.usecase.auth.LogoutUseCase
import com.patagonia.app.domain.usecase.auth.RegisterUseCase
import com.patagonia.app.presentation.auth.AuthUiState
import com.patagonia.app.presentation.auth.AuthViewModel
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * RED phase tests for [AuthViewModel] (Plan 04-01, Task 4).
 *
 * Behavior spec from plan:
 *  - Test 1: emits Loading state when login is triggered
 *  - Test 2: emits Success and navigates on valid login
 *  - Test 3: emits Error with message on invalid credentials (D-24)
 *  - Test 4: emits Error when registering with taken username (D-17)
 *  - Test 5: auto-navigates past auth when existing session found on init
 *  - Test 6: initial state is Idle
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var getSessionUseCase: GetSessionUseCase
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mock()
        registerUseCase = mock()
        getSessionUseCase = mock()
        logoutUseCase = mock()
        whenever(getSessionUseCase.invoke()).thenReturn(flowOf(null))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AuthViewModel(
        loginUseCase = loginUseCase,
        registerUseCase = registerUseCase,
        getSessionUseCase = getSessionUseCase,
        logoutUseCase = logoutUseCase
    )

    @Test
    fun `initial state is Idle`() = runTest {
        val viewModel = createViewModel()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `login emits Loading then Success on valid credentials`() = runTest {
        val profile = UserProfile(
            id = "user-uuid-123",
            email = "user@test.com",
            username = "testuser"
        )
        whenever(loginUseCase.invoke("user@test.com", "password123"))
            .thenReturn(Result.success(profile))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem()) // initial

            viewModel.login("user@test.com", "password123")

            assertEquals(AuthUiState.Loading, awaitItem())
            val successState = awaitItem()
            assertTrue(successState is AuthUiState.Success)
            assertEquals(profile, (successState as AuthUiState.Success).profile)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login emits Error with message on invalid credentials`() = runTest {
        whenever(loginUseCase.invoke("user@test.com", "wrong-password"))
            .thenReturn(Result.failure(AuthException("Invalid credentials")))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())

            viewModel.login("user@test.com", "wrong-password")

            assertEquals(AuthUiState.Loading, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is AuthUiState.Error)
            assertEquals("Invalid credentials", (errorState as AuthUiState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register emits Error when username is taken`() = runTest {
        whenever(registerUseCase.invoke("new@test.com", "password123", "takenuser"))
            .thenReturn(Result.failure(AuthException("Username is already taken")))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())

            viewModel.register("new@test.com", "password123", "takenuser")

            assertEquals(AuthUiState.Loading, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is AuthUiState.Error)
            assertEquals("Username is already taken", (errorState as AuthUiState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register emits Success on valid registration`() = runTest {
        val profile = UserProfile(
            id = "new-uuid-456",
            email = "new@test.com",
            username = "newuser"
        )
        whenever(registerUseCase.invoke("new@test.com", "password123", "newuser"))
            .thenReturn(Result.success(profile))

        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())

            viewModel.register("new@test.com", "password123", "newuser")

            assertEquals(AuthUiState.Loading, awaitItem())
            val successState = awaitItem()
            assertTrue(successState is AuthUiState.Success)
            assertEquals(profile, (successState as AuthUiState.Success).profile)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `auto-navigates past auth when existing session found on init`() = runTest {
        val profile = UserProfile(
            id = "user-uuid-123",
            email = "user@test.com",
            username = "testuser"
        )
        whenever(getSessionUseCase.invoke()).thenReturn(flowOf(profile))

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success)
        assertEquals(profile, (state as AuthUiState.Success).profile)
    }

    @Test
    fun `logout resets state to Idle`() = runTest {
        val profile = UserProfile(
            id = "user-uuid-123",
            email = "user@test.com",
            username = "testuser"
        )
        whenever(loginUseCase.invoke(any(), any()))
            .thenReturn(Result.success(profile))
        whenever(logoutUseCase.invoke())
            .thenReturn(Result.success(Unit))

        val viewModel = createViewModel()
        viewModel.login("user@test.com", "password123")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is AuthUiState.Success)

        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }
}
