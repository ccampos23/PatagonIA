package com.patagonia.app.data.repository

import app.cash.turbine.test
import com.patagonia.app.data.remote.SupabaseAuthDataSource
import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * RED phase tests for [AuthRepositoryImpl] (Plan 04-01, Task 3).
 *
 * Validates that AuthRepositoryImpl correctly delegates to SupabaseAuthDataSource
 * and wraps results in kotlin.Result, translating AuthException to Result.failure.
 */
class AuthRepositoryImplTest {

    private lateinit var dataSource: SupabaseAuthDataSource
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        dataSource = mock()
        repository = AuthRepositoryImpl(dataSource)
    }

    @Test
    fun `login returns success with UserProfile on valid credentials`() = runTest {
        val expected = UserProfile(
            id = "user-uuid-123",
            email = "user@test.com",
            username = "testuser"
        )
        whenever(dataSource.login("user@test.com", "password123")).thenReturn(expected)

        val result = repository.login("user@test.com", "password123")

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `login returns failure with AuthException on invalid credentials`() = runTest {
        whenever(dataSource.login("user@test.com", "wrong-password"))
            .thenThrow(AuthException("Invalid credentials"))

        val result = repository.login("user@test.com", "wrong-password")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AuthException)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register returns success with UserProfile`() = runTest {
        val expected = UserProfile(
            id = "new-uuid-456",
            email = "new@test.com",
            username = "newuser"
        )
        whenever(dataSource.register("new@test.com", "password123", "newuser"))
            .thenReturn(expected)

        val result = repository.register("new@test.com", "password123", "newuser")

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `register returns failure on AuthException`() = runTest {
        whenever(dataSource.register("dup@test.com", "password123", "dupuser"))
            .thenThrow(AuthException("User already registered"))

        val result = repository.register("dup@test.com", "password123", "dupuser")

        assertTrue(result.isFailure)
        assertEquals("User already registered", result.exceptionOrNull()?.message)
    }

    @Test
    fun `logout returns success`() = runTest {
        val result = repository.logout()

        assertTrue(result.isSuccess)
        verify(dataSource).logout()
    }

    @Test
    fun `logout returns failure on AuthException`() = runTest {
        whenever(dataSource.logout()).thenThrow(AuthException("Logout failed"))

        val result = repository.logout()

        assertTrue(result.isFailure)
        assertEquals("Logout failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getCurrentUser returns UserProfile from data source`() = runTest {
        val expected = UserProfile(
            id = "user-uuid-123",
            email = "user@test.com",
            username = "testuser"
        )
        whenever(dataSource.getCurrentSessionSnapshot()).thenReturn(expected)

        val result = repository.getCurrentUser()

        assertEquals(expected, result)
    }

    @Test
    fun `getCurrentUser returns null when no session`() = runTest {
        whenever(dataSource.getCurrentSessionSnapshot()).thenReturn(null)

        val result = repository.getCurrentUser()

        assertNull(result)
    }
}
