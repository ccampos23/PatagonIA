import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.patagonia.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.patagonia.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Mapbox Access Token from local.properties (D-09)
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        val mapboxToken = properties.getProperty("MAPBOX_ACCESS_TOKEN") ?: ""
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"$mapboxToken\"")

        // Supabase credentials from local.properties (Phase 04 — D-02)
        val supabaseUrl = properties.getProperty("SUPABASE_URL") ?: ""
        val supabaseAnonKey = properties.getProperty("SUPABASE_ANON_KEY") ?: ""
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }

    androidResources {
        noCompress.add("tflite")
    }
}

// Supabase Kotlin SDK BOM 3.1.1 + Ktor 3.0.0 are pinned to stay compatible with the project's
// Kotlin 2.1.0 / AGP 8.7.3 / compileSdk 35 toolchain. Newer Supabase (3.6.0) pulls Ktor 3.4.3
// compiled with Kotlin 2.3 metadata, incompatible with Kotlin 2.1. Force both libraries to the
// known-compatible versions. Tracked as deviation in 04-01-SUMMARY.md.
configurations.all {
    resolutionStrategy.force("androidx.browser:browser:1.8.0")
    resolutionStrategy.force("io.ktor:ktor-client-core:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-client-android:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-client-content-negotiation:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-serialization-kotlinx:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-http:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-io:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-utils:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-events:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-network:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-websockets:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-websocket-serialization:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-sse:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-serialization:3.0.0")
    resolutionStrategy.force("io.ktor:ktor-http-cio:3.0.0")
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.54")
    ksp("com.google.dagger:hilt-compiler:2.54")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Ktor Client (upgraded to 3.0.0 for Supabase Kotlin SDK v3.6.0 compat — Phase 04 Task 1)
    implementation("io.ktor:ktor-client-core:3.0.0")
    implementation("io.ktor:ktor-client-android:3.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")

    // Supabase Kotlin SDK (Phase 04 — auth, postgrest, storage)
    // BOM pinned to 3.1.1 (compatible with Kotlin 2.1 + Ktor 3.0; newer 3.6.0 requires Kotlin 2.3+).
    // Tracked as deviation in 04-01-SUMMARY.md.
    val supabaseBom = platform("io.github.jan-tennert.supabase:bom:3.1.1")
    implementation(supabaseBom)
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")

    // WorkManager + Hilt-Work (Phase 04 — needed in Plan 03)
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // CameraX
    val cameraVersion = "1.4.0"
    implementation("androidx.camera:camera-core:$cameraVersion")
    implementation("androidx.camera:camera-camera2:$cameraVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraVersion")
    implementation("androidx.camera:camera-view:$cameraVersion")

    // ML Kit Custom Model Labeling
    implementation("com.google.mlkit:image-labeling-custom:17.0.3")
    implementation("com.google.mlkit:image-labeling:17.0.9")

    // TensorFlow Lite Core
    implementation("org.tensorflow:tensorflow-lite:2.14.0")

    // Explicitly enforce 16 KB aligned graphics-path
    implementation("androidx.graphics:graphics-path:1.1.0")

    // Mapbox Maps SDK for Android
    // NOTE: Uncomment when MAPBOX_DOWNLOADS_TOKEN is set to a real secret token in gradle.properties
    implementation("com.mapbox.maps:android:11.11.1")
    implementation("com.mapbox.extension:maps-compose:11.11.1")

    // Testing
    testImplementation("org.json:json:20231013")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("app.cash.turbine:turbine:1.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.work:work-testing:2.10.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
