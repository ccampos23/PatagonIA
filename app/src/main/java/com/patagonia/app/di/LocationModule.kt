package com.patagonia.app.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.patagonia.app.data.service.LocationTrackerImpl
import com.patagonia.app.domain.repository.LocationTracker
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker(
        impl: LocationTrackerImpl
    ): LocationTracker

    companion object {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }
    }
}
