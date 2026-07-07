package com.patagonia.app.domain.usecase.auth

import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observe the current authentication session as a [Flow].
 *
 * Emits [UserProfile] when the user is authenticated, or null when no session exists.
 * Sessions persist indefinitely until explicit logout (D-04).
 *
 * Used by the presentation layer to gate navigation (show auth screens vs. main app).
 *
 * Introduced in Plan 04-01, Task 3.
 */
class GetSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return authRepository.observeSession()
    }
}
