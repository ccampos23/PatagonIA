package com.patagonia.app.data.di

import android.content.Context
import androidx.room.Room
import com.patagonia.app.data.local.PatagoniaDatabase
import com.patagonia.app.data.local.dao.CaptureDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PatagoniaDatabase {
        return Room.databaseBuilder(
            context,
            PatagoniaDatabase::class.java,
            "patagonia_database"
        ).addMigrations(PatagoniaDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideCaptureDao(database: PatagoniaDatabase): CaptureDao {
        return database.captureDao()
    }
}
