package com.patagonia.app.data.remote

import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Auth data source — wraps the Supabase Auth plugin ([SupabaseAuthApi]) and translates
 * SDK exceptions into domain-level [AuthException] so the repository/use-case layers can
 * show inline error messages on the auth screens (D-24).
 *
 * Introduced in Plan 04-01 Task 2. Does NOT touch STATE.md/ROADMAP.md — orchestrator owns
 * shared-file writes after wave merge in parallel mode.
 */
@Singleton
class SupabaseAuthDataSource @Inject constructor(
    private val api: SupabaseAuthApi
) {

    suspend fun login(email: String, password: String): UserProfile {
        return try {
            api.signInWithEmailAndPassword(email, password)
        } catch (e: Throwable) {
            throw AuthException(e.message ?: "Login failed", e)
        }
    }

    suspend fun register(email: String, password: String, username: String): UserProfile {
        return try {
            api.signUpWithEmailAndPassword(email, password, username)
        } catch (e: Throwable) {
            throw AuthException(e.message ?: "Registration failed", e)
        }
    }

    suspend fun getCurrentSessionSnapshot(): UserProfile? = api.currentSession()

    suspend fun logout() = api.signOut()

    suspend fun updateProfile(bio: String, isPrivate: Boolean): UserProfile {
        return try {
            api.updateProfile(bio, isPrivate)
        } catch (e: Throwable) {
            throw AuthException(e.message ?: "Failed to update profile", e)
        }
    }
}
