package com.patagonia.app.data.remote

import com.patagonia.app.domain.model.UserProfile
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supabase-SDK-backed implementation of [SupabaseAuthApi]. All Supabase Auth DSL calls live
 * here so the rest of the auth layer (SupabaseAuthDataSource, repository, use cases) stays
 * free of Supabase-specific imports and remains unit-testable with plain mocks.
 *
 * Wire-up is owned by [com.patagonia.app.di.SupabaseModule] (Hilt).
 *
 * D-03 (deferred email verification) is honored: signUpWith returns immediately and we map
 * the freshly-registered user to UserProfile without verifying email.
 * D-04 (indefinite session) is honored: the SDK's sessionManager handles persisted sessions
 * via the SupabaseModule configuration; here we just expose the current snapshot.
 *
 * API surface verified against auth-kt 3.1.1 (jar introspection):
 *  - Auth.signInWith(Email) { email=...; password=... } : suspend, returns Unit
 *  - Auth.signUpWith(Email) { email=...; password=...; data=JsonObject } : suspend, returns UserInfo
 *  - Auth.currentUserOrNull() : UserInfo?
 *  - Auth.signOut(scope) : suspend Unit
 *  - UserInfo.id, .email (String?), .userMetadata (JsonObject)
 *  - Email.Config.data is kotlinx.serialization.json.JsonObject
 */
@Singleton
class SupabaseAuthApiImpl @Inject constructor(
    private val auth: Auth
) : SupabaseAuthApi {

    override suspend fun signInWithEmailAndPassword(email: String, password: String): UserProfile {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        val user = auth.currentUserOrNull()
            ?: error("Supabase session was null right after sign-in — SDK should cache the new session")
        return user.toUserProfile()
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ): UserProfile {
        val user = auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject {
                put(USERNAME_METADATA_KEY, JsonPrimitive(username))
            }
        }
        // signUpWith returns a UserInfo for the registered user; if null (rare — e.g.
        // email verification required by server config), fall back to currentUserOrNull.
        return user?.toUserProfile()
            ?: auth.currentUserOrNull()?.toUserProfile()
            ?: error("Supabase signup returned no user and no session")
    }

    override suspend fun currentSession(): UserProfile? {
        return auth.currentUserOrNull()?.toUserProfile()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    private fun UserInfo.toUserProfile(): UserProfile {
        val metadata = userMetadata
        val username = metadata?.get(USERNAME_METADATA_KEY)?.jsonPrimitive?.content ?: ""
        return UserProfile(
            id = id,
            email = email ?: "",
            username = username
        )
    }

    companion object {
        const val USERNAME_METADATA_KEY = "username"
    }
}
