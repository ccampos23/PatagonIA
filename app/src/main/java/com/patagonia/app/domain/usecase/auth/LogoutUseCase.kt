package com.patagonia.app.domain.usecase.auth

import com.patagonia.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Sign out the current user and clear the local session.
 *
 * Returns [Result.success] on successful logout, or [Result.failure] on error.
 * After successful logout, [GetSessionUseCase] will emit null and the presentation
 * layer navigates back to the auth screens.
 *
 * Introduced in Plan 04-01, Task 3.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
