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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * RED phase tests for [RegisterUseCase] (Plan 04-01, Task 3).
 *
 * Behavior spec from plan:
 *  - Test 3: validates username uniqueness before creating account
 *  - Test 4: returns Result.failure on duplicate email
 */
class RegisterUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var registerUseCase: RegisterUseCase

    @Before
    fun setup() {
        authRepository = mock()
        registerUseCase = RegisterUseCase(authRepository)
    }

    @Test
    fun `invoke checks username availability then registers successfully`() = runTest {
        val expected = UserProfile(
            id = "new-uuid-456",
            email = "new@test.com",
            username = "newuser"
        )
        whenever(authRepository.isUsernameAvailable("newuser")).thenReturn(true)
        whenever(authRepository.register("new@test.com", "password123", "newuser"))
            .thenReturn(Result.success(expected))

        val result = registerUseCase("new@test.com", "password123", "newuser")

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
        verify(authRepository).isUsernameAvailable("newuser")
        verify(authRepository).register("new@test.com", "password123", "newuser")
    }

    @Test
    fun `invoke returns failure when username is already taken`() = runTest {
        whenever(authRepository.isUsernameAvailable("takenuser")).thenReturn(false)

        val result = registerUseCase("new@test.com", "password123", "takenuser")

        assertTrue(result.isFailure)
        assertEquals("Username is already taken", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke returns failure on duplicate email`() = runTest {
        whenever(authRepository.isUsernameAvailable("validuser")).thenReturn(true)
        whenever(authRepository.register("dup@test.com", "password123", "validuser"))
            .thenReturn(Result.failure(AuthException("User already registered")))

        val result = registerUseCase("dup@test.com", "password123", "validuser")

        assertTrue(result.isFailure)
        assertEquals("User already registered", result.exceptionOrNull()?.message)
    }
}
