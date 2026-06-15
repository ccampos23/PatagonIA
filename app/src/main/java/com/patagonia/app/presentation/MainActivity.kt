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
import com.patagonia.app.presentation.ui.LoadingScreen
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
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "PatagonIA - ¡Listo para explorar!")
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
