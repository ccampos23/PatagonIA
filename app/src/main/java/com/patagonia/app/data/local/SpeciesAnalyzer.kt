package com.patagonia.app.data.local

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import com.patagonia.app.domain.model.Recognition
import java.io.File

class SpeciesAnalyzer(
    private val context: Context,
    private val onResult: (List<Recognition>) -> Unit
) : ImageAnalysis.Analyzer {

    private var labeler: ImageLabeler? = null
    private var labels = listOf<String>()
    private var lastAnalysisTimestamp = 0L

    init {
        initializeLabeler()
    }

    private fun initializeLabeler() {
        try {
            val modelFile = File(context.filesDir, "species_model.tflite")
            val labelsFile = File(context.filesDir, "species_labels.txt")

            if (modelFile.exists() && labelsFile.exists()) {
                labels = labelsFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }

                val localModel = LocalModel.Builder()
                    .setAbsoluteFilePath(modelFile.absolutePath)
                    .build()

                val options = CustomImageLabelerOptions.Builder(localModel)
                    .setConfidenceThreshold(0.15f)
                    .setMaxResultCount(3)
                    .build()

                labeler = ImageLabeling.getClient(options)
                Log.d("SpeciesAnalyzer", "ML Kit Custom ImageLabeler initialized successfully.")
            } else {
                Log.w("SpeciesAnalyzer", "Model or labels files missing. Falling back to mock analyzer.")
            }
        } catch (e: Exception) {
            Log.e("SpeciesAnalyzer", "Failed to initialize custom labeler, falling back to mock", e)
        }
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalysisTimestamp < 500) {
            imageProxy.close()
            return
        }
        lastAnalysisTimestamp = currentTimestamp

        val currentLabeler = labeler
        if (currentLabeler != null) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                currentLabeler.process(image)
                    .addOnSuccessListener { mlLabels ->
                        val results = mlLabels.map { label ->
                            val labelText = label.text
                            val displayName = try {
                                val index = labelText.toInt()
                                if (index in labels.indices) labels[index] else labelText
                            } catch (e: NumberFormatException) {
                                labelText
                            }
                            Recognition(
                                title = displayName,
                                confidence = label.confidence
                            )
                        }
                        onResult(results)
                        imageProxy.close()
                    }
                    .addOnFailureListener { e ->
                        Log.e("SpeciesAnalyzer", "Image classification failed", e)
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        } else {
            val mockRecognitions = getMockRecognitions()
            onResult(mockRecognitions)
            imageProxy.close()
        }
    }

    private fun getMockRecognitions(): List<Recognition> {
        val mockLabelsList = if (labels.isNotEmpty()) {
            labels
        } else {
            listOf("Puma", "Huemul", "Cóndor", "Guanaco", "Zorro Chilla")
        }

        val timeSec = (System.currentTimeMillis() / 1000)
        val idx1 = (timeSec % mockLabelsList.size).toInt()
        val idx2 = ((timeSec + 1) % mockLabelsList.size).toInt()
        
        return listOf(
            Recognition(mockLabelsList[idx1], 0.82f + (Math.sin(timeSec.toDouble()) * 0.05).toFloat()),
            Recognition(mockLabelsList[idx2], 0.12f + (Math.cos(timeSec.toDouble()) * 0.03).toFloat())
        )
    }
}
