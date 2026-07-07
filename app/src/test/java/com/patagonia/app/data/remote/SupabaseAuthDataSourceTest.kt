package com.patagonia.app.data.remote

import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Task 2 (Plan 04-01) — RED phase tests for SupabaseAuthDataSource.
 *
 * Tests cover the 5 behaviors enumerated in the plan:
 *  1. login returns UserProfile on valid credentials
 *  2. login throws AuthException on invalid credentials
 *  3. register creates user and returns UserProfile
 *  4. register throws AuthException on duplicate email
 *  5. getCurrentSession returns null when no session
 *
 * SupabaseAuthDataSource depends on a thin SupabaseAuthApi wrapper around the Supabase Auth
 * plugin, so the data source can be tested without depending on the Supabase SDK DSL.
 */
class SupabaseAuthDataSourceTest {

    private lateinit var api: SupabaseAuthApi
    private lateinit var dataSource: SupabaseAuthDataSource

    @Before
    fun setup() {
        api = mock()
        dataSource = SupabaseAuthDataSource(api)
    }

    @Test
    fun `login returns UserProfile on valid credentials`() = runTest {
        val expected = UserProfile(
            id = "user-uuid-123",
            email = "user@test.com",
            username = "testuser"
        )
        whenever(api.signInWithEmailAndPassword("user@test.com", "password123")).thenReturn(expected)

        val result = dataSource.login("user@test.com", "password123")

        assertEquals(expected, result)
        verify(api).signInWithEmailAndPassword("user@test.com", "password123")
    }

    @Test
    fun `login throws AuthException on invalid credentials`() = runTest {
        whenever(api.signInWithEmailAndPassword("user@test.com", "wrong-password"))
            .thenThrow(RuntimeException("Invalid credentials"))

        val ex = assertThrows(AuthException::class.java) {
            kotlinx.coroutines.runBlocking {
                dataSource.login("user@test.com", "wrong-password")
            }
        }
        assertEquals("Invalid credentials", ex.message)
    }

    @Test
    fun `register creates user and returns UserProfile`() = runTest {
        val expected = UserProfile(
            id = "new-uuid-456",
            email = "new@test.com",
            username = "newuser"
        )
        whenever(api.signUpWithEmailAndPassword("new@test.com", "password123", "newuser"))
            .thenReturn(expected)

        val result = dataSource.register("new@test.com", "password123", "newuser")

        assertEquals(expected, result)
        verify(api).signUpWithEmailAndPassword("new@test.com", "password123", "newuser")
    }

    @Test
    fun `register throws AuthException on duplicate email`() = runTest {
        whenever(api.signUpWithEmailAndPassword("dup@test.com", "password123", "dupuser"))
            .thenThrow(RuntimeException("User already registered"))

        val ex = assertThrows(AuthException::class.java) {
            kotlinx.coroutines.runBlocking {
                dataSource.register("dup@test.com", "password123", "dupuser")
            }
        }
        assertEquals("User already registered", ex.message)
    }

    @Test
    fun `getCurrentSession returns null when no session`() = runTest {
        whenever(api.currentSession()).thenReturn(null)

        val result = dataSource.getCurrentSessionSnapshot()

        assertNull(result)
        verify(api).currentSession()
    }

    @Test
    fun `logout delegates to api signOut`() = runTest {
        dataSource.logout()

        verify(api).signOut()
    }
}
