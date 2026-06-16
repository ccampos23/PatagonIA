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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    val darkGreenBg = Color(0xFF081C15)
    val lightGreenText = Color(0xFFD8F3DC)
    val accentGreen = Color(0xFF40916C)
    val lightGreenSub = Color(0xFF95D5B2)
    val errorColor = Color(0xFFE63946)

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
            .background(darkGreenBg),
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
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFF1B4332)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌿",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PatagonIA",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = lightGreenText,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu guía de avistamientos offline",
                fontSize = 15.sp,
                color = lightGreenSub,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            when (val status = downloadStatus) {
                is DownloadStatus.Idle -> {
                    CircularProgressIndicator(color = accentGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Conectando...",
                        color = lightGreenSub,
                        fontSize = 14.sp
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
                            color = accentGreen,
                            trackColor = Color(0xFF1B4332),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${status.percentage}%",
                            color = lightGreenText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Descargando base de reconocimiento e iNaturalist...",
                            color = lightGreenSub,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is DownloadStatus.Success -> {
                    CircularProgressIndicator(progress = { 1f }, color = accentGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¡Descarga completa! Iniciando...",
                        color = lightGreenSub,
                        fontSize = 14.sp
                    )
                }
                is DownloadStatus.Error -> {
                    Text(
                        text = "Error de descarga",
                        color = errorColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = status.message,
                        color = Color.LightGray,
                        fontSize = 13.sp,
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
                            colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Reintentar", color = lightGreenText)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = onDownloadComplete,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332)),
                            border = BorderStroke(1.dp, Color(0xFF40916C)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Usar sin internet", color = lightGreenSub)
                        }
                    }
                }
            }
        }
    }
}
