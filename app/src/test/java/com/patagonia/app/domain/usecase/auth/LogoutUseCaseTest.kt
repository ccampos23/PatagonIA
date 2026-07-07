package com.patagonia.app.domain.usecase.auth

import com.patagonia.app.domain.repository.AuthException
import com.patagonia.app.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * RED phase tests for [LogoutUseCase] (Plan 04-01, Task 3).
 *
 * Behavior spec from plan:
 *  - Test 6: clears session and returns success
 */
class LogoutUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setup() {
        authRepository = mock()
        logoutUseCase = LogoutUseCase(authRepository)
    }

    @Test
    fun `invoke clears session and returns success`() = runTest {
        whenever(authRepository.logout()).thenReturn(Result.success(Unit))

        val result = logoutUseCase()

        assertTrue(result.isSuccess)
        verify(authRepository).logout()
    }

    @Test
    fun `invoke returns failure when logout fails`() = runTest {
        whenever(authRepository.logout())
            .thenReturn(Result.failure(AuthException("Logout failed")))

        val result = logoutUseCase()

        assertTrue(result.isFailure)
        assertEquals("Logout failed", result.exceptionOrNull()?.message)
    }
}
