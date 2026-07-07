package com.patagonia.app.data.repository

import com.patagonia.app.data.remote.SupabaseAuthDataSource
import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthException
import com.patagonia.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Concrete implementation of [AuthRepository] delegating to [SupabaseAuthDataSource].
 *
 * All operations that can fail are wrapped in [Result] — [AuthException] from the data source
 * is caught and surfaced as [Result.failure] so the presentation layer can show inline error
 * messages (D-24) without try/catch blocks.
 *
 * Introduced in Plan 04-01, Task 3. Bound via Hilt `@Binds` in
 * [com.patagonia.app.data.di.RepositoryModule].
 */
class AuthRepositoryImpl @Inject constructor(
    private val dataSource: SupabaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val profile = dataSource.login(email, password)
            Result.success(profile)
        } catch (e: AuthException) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<UserProfile> {
        return try {
            val profile = dataSource.register(email, password, username)
            Result.success(profile)
        } catch (e: AuthException) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            dataSource.logout()
            Result.success(Unit)
        } catch (e: AuthException) {
            Result.failure(e)
        }
    }

    override fun observeSession(): Flow<UserProfile?> = flow {
        emit(dataSource.getCurrentSessionSnapshot())
    }

    override suspend fun getCurrentUser(): UserProfile? {
        return dataSource.getCurrentSessionSnapshot()
    }

    override suspend fun isUsernameAvailable(username: String): Boolean {
        // TODO(04-02): Query Supabase profiles table to check username uniqueness.
        // For Plan 04-01, username is stored in user_metadata on registration (D-17).
        // Full uniqueness enforcement requires the profiles table from Plan 04-02.
        // For now, always return true — RegisterUseCase will still catch duplicate-email
        // errors from the data source.
        return true
    }

    override suspend fun updateProfile(bio: String, isPrivate: Boolean): Result<UserProfile> {
        return try {
            val updated = dataSource.updateProfile(bio, isPrivate)
            Result.success(updated)
        } catch (e: AuthException) {
            Result.failure(e)
        }
    }
}
