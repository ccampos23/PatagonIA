package com.patagonia.app.presentation.ui

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patagonia.app.domain.repository.DownloadStatus
import com.patagonia.app.presentation.viewmodel.LoadingViewModel

@Composable
fun LoadingScreen(
    viewModel: LoadingViewModel,
    onDownloadComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val downloadStatus by viewModel.downloadStatus.collectAsState()
    val colors = MaterialTheme.colorScheme
    val type = MaterialTheme.typography

    LaunchedEffect(downloadStatus) {
        if (downloadStatus is DownloadStatus.Idle) {
            viewModel.startDownload()
        } else if (downloadStatus is DownloadStatus.Success) {
            onDownloadComplete()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Forest,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PatagonIA",
                style = type.headlineLarge,
                color = colors.onSurface,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu guía de avistamientos offline",
                style = type.bodyLarge,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            when (val status = downloadStatus) {
                is DownloadStatus.Idle -> {
                    CircularProgressIndicator(color = colors.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Conectando...",
                        style = type.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
                is DownloadStatus.Progress -> {
                    val progressFloat = status.percentage / 100f
                    val animatedProgress by animateFloatAsState(
                        targetValue = progressFloat,
                        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
                        label = "progress"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            color = colors.primary,
                            trackColor = colors.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${status.percentage}%",
                            style = type.titleLarge,
                            color = colors.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Descargando base de reconocimiento e iNaturalist...",
                            style = type.bodyMedium,
                            color = colors.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is DownloadStatus.Success -> {
                    CircularProgressIndicator(progress = { 1f }, color = colors.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¡Descarga completa! Iniciando...",
                        style = type.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
                is DownloadStatus.Error -> {
                    Text(
                        text = "Error de descarga",
                        style = type.titleLarge,
                        color = colors.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = status.message,
                        style = type.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.startDownload() },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = "Reintentar", style = type.labelLarge, color = colors.onPrimary)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = onDownloadComplete,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceVariant),
                            border = BorderStroke(1.dp, colors.primary),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = "Usar sin internet", style = type.labelLarge, color = colors.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
