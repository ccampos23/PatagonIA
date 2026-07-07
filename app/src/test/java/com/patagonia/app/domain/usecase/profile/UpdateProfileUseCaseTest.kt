package com.patagonia.app.domain.usecase.profile

import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UpdateProfileUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: UpdateProfileUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = UpdateProfileUseCase(repository)
    }

    @Test
    fun `invoke calls repository updateProfile`() = runTest {
        val expected = UserProfile(
            id = "user-123",
            email = "user@test.com",
            username = "testuser",
            bio = "New bio",
            isPrivate = false
        )
        whenever(repository.updateProfile("New bio", false)).thenReturn(Result.success(expected))

        val result = useCase("New bio", false)

        assertEquals(Result.success(expected), result)
        verify(repository).updateProfile("New bio", false)
    }
}
