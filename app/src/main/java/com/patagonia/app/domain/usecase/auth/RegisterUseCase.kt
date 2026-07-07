package com.patagonia.app.domain.usecase.auth

import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthException
import com.patagonia.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Register a new user with email, password, and unique username.
 *
 * Checks username availability (D-17) before attempting registration.
 * Email verification is deferred (D-03) — user can immediately use the app after sign-up.
 *
 * Returns [Result.success] with [UserProfile] on successful registration,
 * or [Result.failure] if the username is taken or registration fails (duplicate email, etc.).
 *
 * Introduced in Plan 04-01, Task 3.
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String
    ): Result<UserProfile> {
        if (!authRepository.isUsernameAvailable(username)) {
            return Result.failure(AuthException("Username is already taken"))
        }
        return authRepository.register(email, password, username)
    }
}
