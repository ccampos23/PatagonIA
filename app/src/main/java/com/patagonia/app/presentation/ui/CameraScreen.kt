package com.patagonia.app.presentation.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.layout.Row
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.collectAsState
import com.patagonia.app.data.local.SpeciesAnalyzer
import com.patagonia.app.presentation.viewmodel.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onPhotoCaptured: (String, List<com.patagonia.app.domain.model.Recognition>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val recognitions by viewModel.recognitions.collectAsState()
    val colors = MaterialTheme.colorScheme
    val type = MaterialTheme.typography

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val speciesAnalyzer = remember {
        SpeciesAnalyzer(context) { results ->
            viewModel.updateRecognitions(results)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                Log.d("CameraScreen", "Gallery URI received: $uri")
                val destFile = File(context.filesDir, "gallery_${System.currentTimeMillis()}.jpg")
                if (copyUriToFile(context, uri, destFile)) {
                    Log.d("CameraScreen", "Image copied to: ${destFile.absolutePath} (size=${destFile.length()} bytes)")
                    
                    // Run the analysis in a background thread to prevent UI freezing
                    coroutineScope.launch(Dispatchers.Default) {
                        try {
                            val bitmap = BitmapFactory.decodeFile(destFile.absolutePath)
                            if (bitmap != null) {
                                // Read EXIF rotation and rotate bitmap
                                val rotation = getExifRotation(destFile.absolutePath)
                                val rotatedBitmap = if (rotation != 0) {
                                    val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                                } else {
                                    bitmap
                                }

                                Log.d("CameraScreen", "Bitmap decoded successfully (exif rotation = $rotation), starting static analysis...")
                                speciesAnalyzer.analyzeStaticImage(rotatedBitmap) { results ->
                                    Log.d("CameraScreen", "Gallery analysis complete: ${results.size} results")
                                    results.forEachIndexed { i, r ->
                                        Log.d("CameraScreen", "  Result[$i]: ${r.title} (${(r.confidence * 100).toInt()}%) sci=${r.scientificName}")
                                    }
                                    // Ensure we navigate on the main thread for Compose state safety
                                    ContextCompat.getMainExecutor(context).execute {
                                        onPhotoCaptured(destFile.absolutePath, results)
                                    }
                                }
                            } else {
                                Log.e("CameraScreen", "Failed to decode bitmap from copied file")
                                ContextCompat.getMainExecutor(context).execute {
                                    Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("CameraScreen", "Failed to load static image", e)
                            ContextCompat.getMainExecutor(context).execute {
                                Toast.makeText(context, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Log.e("CameraScreen", "Failed to copy gallery image to local storage")
                    Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(cameraExecutor, speciesAnalyzer)
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture,
                                imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraScreen", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize(),
                update = { /* Unbinding and binding is handled once in factory; lifecycle owner handles pausing */ }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de Galería
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(colors.background.copy(alpha = 0.7f))
                        .border(BorderStroke(1.5.dp, colors.primary), CircleShape)
                        .clickable {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Abrir galería",
                        tint = colors.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Botón Obturador
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(BorderStroke(4.dp, Color.White), CircleShape)
                        .clickable {
                            takePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                cameraExecutor = cameraExecutor,
                                onPhotoCaptured = { path ->
                                    onPhotoCaptured(path, recognitions)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                // Spacer para balancear
                Spacer(modifier = Modifier.size(56.dp))
            }

            val topRecognition = recognitions.firstOrNull()
            if (topRecognition != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(colors.background.copy(alpha = 0.85f))
                            .border(BorderStroke(1.dp, colors.primary), MaterialTheme.shapes.medium)
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        val percentage = (topRecognition.confidence * 100).toInt()
                        Text(
                            text = "${topRecognition.title} ($percentage%)",
                            color = colors.onSurface,
                            style = type.titleMedium
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Permiso de Cámara Requerido",
                    style = type.headlineSmall,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Para poder utilizar el avistamiento e identificar especies silvestres, debes otorgar permiso de cámara.",
                    style = type.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { launcher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(text = "Otorgar permiso", color = colors.onPrimary, style = type.labelLarge)
                }
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    cameraExecutor: ExecutorService,
    onPhotoCaptured: (String) -> Unit
) {
    val photoFile = File(
        context.filesDir,
        "capture_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed: ${exc.message}", exc)
                ContextCompat.getMainExecutor(context).execute {
                    Toast.makeText(context, "Error al capturar foto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = photoFile.absolutePath
                Log.d("CameraScreen", "Photo capture succeeded: $savedUri")
                ContextCompat.getMainExecutor(context).execute {
                    onPhotoCaptured(savedUri)
                }
            }
        }
    )
}

private fun copyUriToFile(context: Context, uri: android.net.Uri, destFile: File): Boolean {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            destFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        true
    } catch (e: Exception) {
        Log.e("CameraScreen", "Failed to copy image URI to local file", e)
        false
    }
}

private fun getExifRotation(filePath: String): Int {
    return try {
        val exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    } catch (e: Exception) {
        Log.e("CameraScreen", "Failed to read EXIF orientation", e)
        0
    }
}
