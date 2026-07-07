package com.patagonia.app.domain.repository

import com.patagonia.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Domain-layer contract for authentication operations.
 *
 * Introduced in Plan 04-01, Task 3. Implemented by [com.patagonia.app.data.repository.AuthRepositoryImpl]
 * which delegates to [com.patagonia.app.data.remote.SupabaseAuthDataSource].
 *
 * All methods that can fail return [Result] so the presentation layer can display
 * inline error messages (D-24) without catching exceptions.
 */
interface AuthRepository {

    /**
     * Authenticate a user with email and password.
     * Returns [Result.success] with [UserProfile] on valid credentials,
     * or [Result.failure] with [AuthException] on invalid credentials.
     */
    suspend fun login(email: String, password: String): Result<UserProfile>

    /**
     * Register a new user with email, password, and unique username.
     * Email verification is deferred (D-03) — the user can immediately log in after registration.
     * Returns [Result.success] with [UserProfile] or [Result.failure] on duplicate email/error.
     */
    suspend fun register(email: String, password: String, username: String): Result<UserProfile>

    /**
     * Sign out the current user and clear the local session.
     * Returns [Result.success] on success or [Result.failure] on error.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Observe the current session as a [Flow]. Emits [UserProfile] when authenticated
     * or null when no session exists. Session persists indefinitely until explicit logout (D-04).
     */
    fun observeSession(): Flow<UserProfile?>

    /**
     * Retrieve the current authenticated user, or null if no session exists.
     * This is a one-shot snapshot, not a reactive stream.
     */
    suspend fun getCurrentUser(): UserProfile?

    /**
     * Check whether a given username is available for registration (D-17).
     * Returns true if the username is not taken.
     */
    suspend fun isUsernameAvailable(username: String): Boolean

    /**
     * Update the authenticated user's profile metadata (bio, privacy setting).
     * Returns [Result.success] with updated [UserProfile] or [Result.failure] on error.
     */
    suspend fun updateProfile(bio: String, isPrivate: Boolean): Result<UserProfile>
}
