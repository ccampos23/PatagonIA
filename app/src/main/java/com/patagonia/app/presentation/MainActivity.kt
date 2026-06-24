package com.patagonia.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.patagonia.app.presentation.theme.CaptureTheme
import com.patagonia.app.presentation.theme.PatagoniaTheme
import com.patagonia.app.presentation.ui.CameraScreen
import com.patagonia.app.presentation.ui.LoadingScreen
import com.patagonia.app.presentation.ui.ReviewScreen
import com.patagonia.app.presentation.map.MapScreen
import com.patagonia.app.presentation.map.AssetManagerScreen
import dagger.hilt.android.AndroidEntryPoint

enum class TestingScreen {
    CAMERA, MAP, DOWNLOADS
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatagoniaTheme {
                CaptureTheme {
                    var showMainApp by remember { mutableStateOf(false) }
                    var currentScreen by remember { mutableStateOf(TestingScreen.CAMERA) }

                    if (showMainApp) {
                        Scaffold(
                            bottomBar = {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = currentScreen == TestingScreen.CAMERA,
                                        onClick = { currentScreen = TestingScreen.CAMERA },
                                        icon = { Icon(Icons.Default.PhotoCamera, contentDescription = "Camera") },
                                        label = { Text("Camera") }
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == TestingScreen.MAP,
                                        onClick = { currentScreen = TestingScreen.MAP },
                                        icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                                        label = { Text("Map") }
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == TestingScreen.DOWNLOADS,
                                        onClick = { currentScreen = TestingScreen.DOWNLOADS },
                                        icon = { Icon(Icons.Default.Download, contentDescription = "Downloads") },
                                        label = { Text("Downloads") }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (currentScreen) {
                                    TestingScreen.CAMERA -> {
                                        val cameraViewModel: com.patagonia.app.presentation.viewmodel.CameraViewModel = hiltViewModel()
                                        var capturedPhotoPath by remember { mutableStateOf<String?>(null) }
                                        var captureRecognitions by remember { mutableStateOf<List<com.patagonia.app.domain.model.Recognition>>(emptyList()) }
                                        
                                        val path = capturedPhotoPath
                                        if (path == null) {
                                            CameraScreen(
                                                viewModel = cameraViewModel,
                                                onPhotoCaptured = { imagePath, results ->
                                                    captureRecognitions = results
                                                    capturedPhotoPath = imagePath
                                                }
                                            )
                                        } else {
                                            ReviewScreen(
                                                imagePath = path,
                                                recognitions = captureRecognitions,
                                                onSave = { name, scientificName, notes, confidence ->
                                                    cameraViewModel.saveCapture(name, scientificName, notes, path, confidence)
                                                    capturedPhotoPath = null
                                                    captureRecognitions = emptyList()
                                                },
                                                onRetake = {
                                                    capturedPhotoPath = null
                                                    captureRecognitions = emptyList()
                                                }
                                            )
                                        }
                                    }
                                    TestingScreen.MAP -> {
                                        MapScreen(modifier = Modifier.fillMaxSize())
                                    }
                                    TestingScreen.DOWNLOADS -> {
                                        AssetManagerScreen(
                                            onBackClick = { currentScreen = TestingScreen.CAMERA },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        val loadingViewModel: com.patagonia.app.presentation.viewmodel.LoadingViewModel = hiltViewModel()
                        LoadingScreen(
                            viewModel = loadingViewModel,
                            onDownloadComplete = {
                                showMainApp = true
                            }
                        )
                    }
                }
            }
        }
    }
}


