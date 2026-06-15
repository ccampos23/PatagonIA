package com.patagonia.app.di

import android.content.Context
import com.patagonia.app.data.remote.KtorModelDownloader
import com.patagonia.app.domain.repository.ModelDownloader
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindModelDownloader(
        impl: KtorModelDownloader
    ): ModelDownloader

    companion object {
        
        @Provides
        @Singleton
        fun provideHttpClient(): HttpClient {
            return HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        coerceInputValues = true
                    })
                }
            }
        }
    }
}
