package com.patagonia.app.presentation.ui

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patagonia.app.domain.model.Recognition
import java.io.File

@Composable
fun ReviewScreen(
    imagePath: String,
    recognitions: List<Recognition>,
    onSave: (String, String?, Float?) -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val topRecognition = remember(recognitions) { recognitions.firstOrNull() }
    
    var speciesName by remember { mutableStateOf(topRecognition?.title ?: "") }
    var notes by remember { mutableStateOf("") }
    var showDetails by remember { mutableStateOf(false) }

    val darkGreenBg = Color(0xFF081C15)
    val lightGreenText = Color(0xFFD8F3DC)
    val accentGreen = Color(0xFF40916C)
    val lightGreenSub = Color(0xFF95D5B2)

    val bitmap = remember(imagePath) {
        BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = darkGreenBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Revisar Avistamiento",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = lightGreenText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B4332)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Foto capturada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "No se pudo cargar la imagen",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = speciesName,
                    onValueChange = { speciesName = it },
                    label = { Text("Nombre de la Especie") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentGreen,
                        unfocusedBorderColor = Color(0xFF1B4332),
                        focusedLabelColor = lightGreenSub,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = lightGreenText,
                        unfocusedTextColor = lightGreenText
                    )
                )

                if (recognitions.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (showDetails) accentGreen else Color(0xFF1B4332))
                            .clickable { showDetails = !showDetails },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ℹ",
                            color = lightGreenText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (showDetails && recognitions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B4332).copy(alpha = 0.5f))
                        .border(BorderStroke(1.dp, Color(0xFF1B4332)), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Resultados de Identificación IA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = lightGreenSub,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    recognitions.take(3).forEach { recognition ->
                        val pct = (recognition.confidence * 100).toInt()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = recognition.title, color = lightGreenText, fontSize = 14.sp)
                            Text(text = "$pct%", color = accentGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
                            Toast.makeText(context, "Reporte enviado para corrección de IA", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52B788).copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text(text = "Reportar coincidencia incorrecta", color = Color(0xFF52B788), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas u observaciones (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentGreen,
                    unfocusedBorderColor = Color(0xFF1B4332),
                    focusedLabelColor = lightGreenSub,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = lightGreenText,
                    unfocusedTextColor = lightGreenText
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val file = File(imagePath)
                        if (file.exists()) {
                            file.delete()
                        }
                        onRetake()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color(0xFF1B4332)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(text = "Reintentar", color = lightGreenSub)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (speciesName.isNotBlank()) {
                            onSave(speciesName, notes.ifBlank { null }, topRecognition?.confidence)
                        }
                    },
                    enabled = speciesName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentGreen,
                        disabledContainerColor = Color(0xFF1B4332).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Guardar",
                        color = if (speciesName.isNotBlank()) lightGreenText else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
