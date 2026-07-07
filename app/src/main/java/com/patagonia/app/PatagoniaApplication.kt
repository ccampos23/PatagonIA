package com.patagonia.app

import android.app.Application
import android.util.Log
import com.patagonia.app.data.config.MapboxStorageConfig
import dagger.hilt.android.HiltAndroidApp
import com.mapbox.common.MapboxOptions

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import javax.inject.Inject

@HiltAndroidApp
class PatagoniaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // Set Mapbox access token globally at application launch (D-10)
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
        
        configureTileStore()
    }


    /**
     * Configure Mapbox TileStore to use internal storage (D-24)
     * and set the ambient cache quota to 250MB (D-25).
     *
     * NOTE: Actual Mapbox TileStore API calls are commented out until
     * the Mapbox SDK is fully integrated. The config values are resolved
     * and logged for verification.
     */
    private fun configureTileStore() {
        val tileStorePath = MapboxStorageConfig.resolveTileStorePath(filesDir)
        val cacheSizeMb = MapboxStorageConfig.getAmbientCacheSizeMb()

        Log.d(TAG, "TileStore path: $tileStorePath")
        Log.d(TAG, "Ambient cache limit: ${cacheSizeMb}MB")

        // TODO: Uncomment when Mapbox SDK TileStore API is available
        // val tileStore = TileStore.create(tileStorePath)
        // tileStore.setOption(
        //     TileStoreOptions.MAPBOX_ACCESS_TOKEN,
        //     TileDataDomain.MAPS,
        //     Value(BuildConfig.MAPBOX_ACCESS_TOKEN)
        // )
        // MapboxOptions.mapsOptions.tileStore = tileStore
    }

    companion object {
        private const val TAG = "PatagoniaApp"
    }
}

