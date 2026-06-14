package com.patagonia.app.data.di

import com.patagonia.app.data.repository.CaptureRepositoryImpl
import com.patagonia.app.domain.repository.CaptureRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCaptureRepository(
        impl: CaptureRepositoryImpl
    ): CaptureRepository
}
