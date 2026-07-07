package com.patagonia.app.domain.usecase.auth

import app.cash.turbine.test
import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * RED phase tests for [GetSessionUseCase] (Plan 04-01, Task 3).
 *
 * Behavior spec from plan:
 *  - Test 5: emits current session state as Flow
 */
class GetSessionUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var getSessionUseCase: GetSessionUseCase

    @Before
    fun setup() {
        authRepository = mock()
        getSessionUseCase = GetSessionUseCase(authRepository)
    }

    @Test
    fun `invoke emits authenticated user profile from session`() = runTest {
        val profile = UserProfile(
            id = "user-uuid-123",
            email = "user@test.com",
            username = "testuser"
        )
        whenever(authRepository.observeSession()).thenReturn(flowOf(profile))

        getSessionUseCase().test {
            assertEquals(profile, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits null when no session exists`() = runTest {
        whenever(authRepository.observeSession()).thenReturn(flowOf(null))

        getSessionUseCase().test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }
}
