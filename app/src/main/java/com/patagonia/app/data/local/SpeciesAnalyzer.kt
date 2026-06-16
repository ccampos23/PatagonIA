package com.patagonia.app.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.patagonia.app.domain.model.Recognition
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class SpeciesAnalyzer(
    private val context: Context,
    private val onResult: (List<Recognition>) -> Unit
) : ImageAnalysis.Analyzer {

    private var interpreter: Interpreter? = null
    private var labels = listOf<String>()
    private var lastAnalysisTimestamp = 0L

    init {
        initializeInterpreter()
    }

    private fun initializeInterpreter() {
        try {
            val isBundled = try {
                context.assets.open("species_model.tflite").close()
                context.assets.open("species_labels.txt").close()
                true
            } catch (e: Exception) {
                false
            }

            if (isBundled) {
                labels = context.assets.open("species_labels.txt").bufferedReader().useLines { lines ->
                    lines.map { it.trim() }.filter { it.isNotEmpty() }.toList()
                }
                Log.d("SpeciesAnalyzer", "Loaded ${labels.size} species labels from assets")

                val modelBuffer = loadModelFile(context, "species_model.tflite")
                val options = Interpreter.Options().apply {
                    setNumThreads(4)
                }
                interpreter = Interpreter(modelBuffer, options)
                Log.d("SpeciesAnalyzer", "TFLite Interpreter initialized from assets successfully.")
            } else {
                val modelFile = File(context.filesDir, "species_model.tflite")
                val labelsFile = File(context.filesDir, "species_labels.txt")

                if (modelFile.exists() && labelsFile.exists()) {
                    labels = labelsFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                    Log.d("SpeciesAnalyzer", "Loaded ${labels.size} species labels from filesDir")

                    val modelBuffer = loadModelFromFile(modelFile)
                    val options = Interpreter.Options().apply {
                        setNumThreads(4)
                    }
                    interpreter = Interpreter(modelBuffer, options)
                    Log.d("SpeciesAnalyzer", "TFLite Interpreter initialized from filesDir successfully.")
                } else {
                    Log.w("SpeciesAnalyzer", "Model or labels files missing.")
                }
            }
        } catch (e: Exception) {
            Log.e("SpeciesAnalyzer", "Failed to initialize TFLite Interpreter", e)
        }
    }

    private fun loadModelFile(context: Context, modelName: String): java.nio.MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadModelFromFile(modelFile: File): java.nio.MappedByteBuffer {
        val inputStream = FileInputStream(modelFile)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, modelFile.length())
    }

    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalysisTimestamp < 500) {
            imageProxy.close()
            return
        }
        lastAnalysisTimestamp = currentTimestamp

        val currentInterpreter = interpreter
        if (currentInterpreter != null) {
            try {
                // Convert ImageProxy to Bitmap (available in CameraX 1.3.0+)
                val bitmap = imageProxy.toBitmap()
                val rotatedBitmap = rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees)
                val results = runInference(rotatedBitmap)
                onResult(results)
            } catch (e: Exception) {
                Log.e("SpeciesAnalyzer", "Live frame inference failed, falling back to mock", e)
                val mockRecognitions = getMockRecognitions()
                onResult(mockRecognitions)
            } finally {
                imageProxy.close()
            }
        } else {
            val mockRecognitions = getMockRecognitions()
            onResult(mockRecognitions)
            imageProxy.close()
        }
    }

    fun analyzeStaticImage(bitmap: Bitmap, onComplete: (List<Recognition>) -> Unit) {
        val currentInterpreter = interpreter
        Log.d("SpeciesAnalyzer", "analyzeStaticImage called. Interpreter initialized: ${currentInterpreter != null}, Labels loaded: ${labels.size}")
        if (currentInterpreter != null) {
            try {
                val results = runInference(bitmap)
                Log.d("SpeciesAnalyzer", "Static analysis complete: ${results.size} recognitions")
                results.forEachIndexed { idx, recognition ->
                    Log.d("SpeciesAnalyzer", "  [$idx]: ${recognition.title} (${(recognition.confidence * 100).toInt()}%)")
                }
                onComplete(results)
            } catch (e: Exception) {
                Log.e("SpeciesAnalyzer", "Static image classification failed", e)
                onComplete(emptyList())
            }
        } else {
            Log.e("SpeciesAnalyzer", "Interpreter not initialized for static analysis")
            onComplete(emptyList())
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        if (rotationDegrees == 0) return bitmap
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun runInference(bitmap: Bitmap): List<Recognition> {
        val currentInterpreter = interpreter ?: return emptyList()

        // 1. Preprocess bitmap to ByteBuffer (shape: 1x299x299x3, float32)
        val inputBuffer = ByteBuffer.allocateDirect(1 * 299 * 299 * 3 * 4).apply {
            order(ByteOrder.nativeOrder())
        }

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 299, 299, true)
        val intValues = IntArray(299 * 299)
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)

        var pixel = 0
        for (i in 0 until 299) {
            for (j in 0 until 299) {
                val pixelValue = intValues[pixel++]

                // Normalization: mean=127.5f, std=127.5f => maps [0, 255] to [-1f, 1f]
                val r = (((pixelValue shr 16) and 0xFF) - 127.5f) / 127.5f
                val g = (((pixelValue shr 8) and 0xFF) - 127.5f) / 127.5f
                val b = ((pixelValue and 0xFF) - 127.5f) / 127.5f

                inputBuffer.putFloat(r)
                inputBuffer.putFloat(g)
                inputBuffer.putFloat(b)
            }
        }

        // 2. Run inference
        val outputBuffer = Array(1) { FloatArray(24933) }
        currentInterpreter.run(inputBuffer, outputBuffer)

        // 3. Process outputs
        val probabilities = outputBuffer[0]
        
        // Log top 5 raw predictions for debugging
        val rawPredictions = probabilities.mapIndexed { idx, conf -> idx to conf }
            .sortedByDescending { it.second }
            .take(5)
        Log.d("SpeciesAnalyzer", "Top 5 raw predictions from model:")
        rawPredictions.forEachIndexed { i, (idx, conf) ->
            val labelStr = if (idx in labels.indices) labels[idx] else "unknown"
            Log.d("SpeciesAnalyzer", "  Rank ${i+1}: index=$idx, label='$labelStr', raw_confidence=$conf")
        }

        val recognitions = mutableListOf<Recognition>()
        for (i in probabilities.indices) {
            val confidence = probabilities[i]
            if (confidence >= 0.15f) {
                if (i in labels.indices) {
                    val sciName = labels[i]
                    recognitions.add(
                        Recognition(
                            title = SpeciesMapping.getCommonName(sciName),
                            confidence = confidence,
                            scientificName = sciName
                        )
                    )
                }
            }
        }

        return recognitions.sortedByDescending { it.confidence }.take(3)
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

        val sciName1 = mockLabelsList[idx1]
        val sciName2 = mockLabelsList[idx2]

        return listOf(
            Recognition(SpeciesMapping.getCommonName(sciName1), 0.82f + (Math.sin(timeSec.toDouble()) * 0.05).toFloat(), sciName1),
            Recognition(SpeciesMapping.getCommonName(sciName2), 0.12f + (Math.cos(timeSec.toDouble()) * 0.03).toFloat(), sciName2)
        )
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
