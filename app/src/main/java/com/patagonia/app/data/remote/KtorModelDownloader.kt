package com.patagonia.app.data.remote

import android.content.Context
import com.patagonia.app.domain.repository.DownloadStatus
import com.patagonia.app.domain.repository.ModelDownloader
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KtorModelDownloader @Inject constructor(
    private val client: HttpClient,
    @ApplicationContext private val context: Context
) : ModelDownloader {

    private val sharedPrefs = context.getSharedPreferences("model_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_KEY_DOWNLOADED = "model_downloaded"
        const val MODEL_FILENAME = "species_model.tflite"
        const val LABELS_FILENAME = "species_labels.txt"
    }

    override fun isModelDownloaded(): Boolean {
        val hasFlag = sharedPrefs.getBoolean(PREF_KEY_DOWNLOADED, false)
        val modelFile = File(context.filesDir, MODEL_FILENAME)
        val labelsFile = File(context.filesDir, LABELS_FILENAME)
        return hasFlag && modelFile.exists() && labelsFile.exists()
    }

    override fun markModelDownloaded(downloaded: Boolean) {
        sharedPrefs.edit().putBoolean(PREF_KEY_DOWNLOADED, downloaded).apply()
    }

    override fun downloadModelFiles(modelUrl: String, labelsUrl: String): Flow<DownloadStatus> = flow {
        emit(DownloadStatus.Progress(0))

        val modelFileTmp = File(context.filesDir, "$MODEL_FILENAME.tmp")
        val labelsFileTmp = File(context.filesDir, "$LABELS_FILENAME.tmp")

        try {
            // Delete any existing temp files from previous failed downloads
            if (modelFileTmp.exists()) modelFileTmp.delete()
            if (labelsFileTmp.exists()) labelsFileTmp.delete()

            // 1. Download Model File (maps to 0% - 90% progress)
            downloadFileWithProgress(modelUrl, modelFileTmp) { progress ->
                val scaledProgress = (progress * 0.9).toInt()
                emit(DownloadStatus.Progress(scaledProgress))
            }

            // 2. Download Labels File (maps to 90% - 98% progress)
            emit(DownloadStatus.Progress(90))
            downloadFileWithProgress(labelsUrl, labelsFileTmp) { progress ->
                val scaledProgress = 90 + (progress * 0.08).toInt()
                emit(DownloadStatus.Progress(scaledProgress))
            }

            // 3. Verify and Rename (99% - 100% progress)
            emit(DownloadStatus.Progress(99))
            val finalModelFile = File(context.filesDir, MODEL_FILENAME)
            val finalLabelsFile = File(context.filesDir, LABELS_FILENAME)

            if (finalModelFile.exists()) finalModelFile.delete()
            if (finalLabelsFile.exists()) finalLabelsFile.delete()

            if (modelFileTmp.renameTo(finalModelFile) && labelsFileTmp.renameTo(finalLabelsFile)) {
                markModelDownloaded(true)
                emit(DownloadStatus.Success)
            } else {
                throw IllegalStateException("No se pudieron renombrar los archivos temporales a su destino final")
            }

        } catch (e: Exception) {
            // Clean up temporary files on error
            if (modelFileTmp.exists()) modelFileTmp.delete()
            if (labelsFileTmp.exists()) labelsFileTmp.delete()
            markModelDownloaded(false)
            emit(DownloadStatus.Error(e.localizedMessage ?: "Ocurrió un error en la descarga"))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun downloadFileWithProgress(
        url: String,
        destinationFile: File,
        onProgress: suspend (Int) -> Unit
    ) {
        val response = client.get(url) {
            onDownload { bytesSentTotal, contentLength ->
                if (contentLength > 0) {
                    val progress = ((bytesSentTotal * 100) / contentLength).toInt()
                    onProgress(progress)
                }
            }
        }
        
        val inputStream = response.bodyAsChannel().toInputStream()
        destinationFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}
