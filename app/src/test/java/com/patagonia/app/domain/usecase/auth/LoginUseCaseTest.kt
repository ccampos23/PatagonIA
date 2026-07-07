package com.patagonia.app.domain.usecase.auth

import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthException
import com.patagonia.app.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * RED phase tests for [LoginUseCase] (Plan 04-01, Task 3).
 *
 * Behavior spec from plan:
 *  - Test 1: returns Result.success with UserProfile on valid credentials
 *  - Test 2: returns Result.failure with error message on invalid credentials
 */
class LoginUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setup() {
        authRepository = mock()
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `invoke returns success with UserProfile on valid credentials`() = runTest {
        val expected = UserProfile(
            id = "user-uuid-123",
            email = "user@test.com",
            username = "testuser"
        )
        whenever(authRepository.login("user@test.com", "password123"))
            .thenReturn(Result.success(expected))

        val result = loginUseCase("user@test.com", "password123")

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `invoke returns failure with error message on invalid credentials`() = runTest {
        whenever(authRepository.login("user@test.com", "wrong-password"))
            .thenReturn(Result.failure(AuthException("Invalid credentials")))

        val result = loginUseCase("user@test.com", "wrong-password")

        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }
}
