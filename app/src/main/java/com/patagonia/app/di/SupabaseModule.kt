package com.patagonia.app.di

import com.patagonia.app.BuildConfig
import com.patagonia.app.data.remote.SupabaseAuthApi
import com.patagonia.app.data.remote.SupabaseAuthApiImpl
import com.patagonia.app.data.remote.SupabaseCaptureApi
import com.patagonia.app.data.remote.SupabaseCaptureApiImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Singleton

/**
 * Hilt module owning all Supabase-provided dependencies (Phase 04 — Plan 04-01 Task 2).
 *
 * - [provideSupabaseClient]: top-level client singleton with Auth + Postgrest installed
 *   (storage-kt is on the classpath for Plan 04-02 / 04-03 to use; not installed yet).
 * - [provideAuth] / [providePostgrest]: typed plugin accessors pulled off the client singleton.
 * - [bindSupabaseAuthApi]: maps the auth API contract to its Supabase-backed implementation.
 *
 * Supabase credentials are read from BuildConfig (populated from local.properties — D-02/T-04-02).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SupabaseModule {

    @Binds
    @Singleton
    abstract fun bindSupabaseAuthApi(impl: SupabaseAuthApiImpl): SupabaseAuthApi

    @Binds
    @Singleton
    abstract fun bindSupabaseCaptureApi(impl: SupabaseCaptureApiImpl): SupabaseCaptureApi

    companion object {

        @Provides
        @Singleton
        fun provideSupabaseClient(): SupabaseClient {
            return createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseKey = BuildConfig.SUPABASE_ANON_KEY
            ) {
                install(Auth)
                install(Postgrest)
            }
        }

        @Provides
        @Singleton
        fun provideAuth(client: SupabaseClient): Auth = client.auth

        @Provides
        @Singleton
        fun providePostgrest(client: SupabaseClient): Postgrest = client.postgrest
    }
}
