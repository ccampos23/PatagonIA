package com.patagonia.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.patagonia.app.presentation.theme.PatagoniaTheme
import com.patagonia.app.presentation.ui.CameraScreen
import com.patagonia.app.presentation.ui.LoadingScreen
import com.patagonia.app.presentation.ui.ReviewScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatagoniaTheme {
                var showMainApp by remember { mutableStateOf(false) }

                if (showMainApp) {
                    var capturedPhotoPath by remember { mutableStateOf<String?>(null) }
                    
                    val path = capturedPhotoPath
                    if (path == null) {
                        CameraScreen(
                            onPhotoCaptured = { imagePath ->
                                capturedPhotoPath = imagePath
                            }
                        )
                    } else {
                        val cameraViewModel: com.patagonia.app.presentation.viewmodel.CameraViewModel = hiltViewModel()
                        ReviewScreen(
                            imagePath = path,
                            onSave = { name, notes ->
                                cameraViewModel.saveCapture(name, notes, path)
                                capturedPhotoPath = null
                            },
                            onRetake = {
                                capturedPhotoPath = null
                            }
                        )
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
