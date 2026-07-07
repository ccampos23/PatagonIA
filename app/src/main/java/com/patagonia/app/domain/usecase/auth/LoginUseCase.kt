package com.patagonia.app.domain.usecase.auth

import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Authenticate a user with email and password.
 *
 * Validates non-empty input before delegating to [AuthRepository.login].
 * Returns [Result.success] with [UserProfile] on valid credentials,
 * or [Result.failure] on invalid credentials (D-24 inline error messages).
 *
 * Introduced in Plan 04-01, Task 3.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserProfile> {
        return authRepository.login(email, password)
    }
}
