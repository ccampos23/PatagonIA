package com.patagonia.app.data.remote

import com.patagonia.app.domain.model.UserProfile

/**
 * Thin wrapper around the Supabase Auth plugin so [SupabaseAuthDataSource] can be unit-tested
 * without depending on the Supabase SDK DSL (which is hard to mock).
 *
 * The Supabase-backed implementation [SupabaseAuthApiImpl] translates each call into the
 * corresponding Supabase Auth call and maps the result into a [UserProfile] domain object.
 */
interface SupabaseAuthApi {

    suspend fun signInWithEmailAndPassword(email: String, password: String): UserProfile

    suspend fun signUpWithEmailAndPassword(email: String, password: String, username: String): UserProfile

    suspend fun currentSession(): UserProfile?

    suspend fun signOut()

    suspend fun updateProfile(bio: String, isPrivate: Boolean): UserProfile
}
