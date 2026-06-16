# Phase 2: Camera & ML Kit Integration - Execution Plan

**Goal:** Integrate real-time on-device species recognition using CameraX and ML Kit Custom Models.
**Context:** First-launch blocking download for models (D-07), Custom .tflite model (D-03), Top-1 live preview with Top-3 details (D-04), Manual tap with review (D-05), Live continuous recognition with frame dropping (D-06).

## Plan 02-01: Initial First-Launch Model Download
**Objective:** Implement the first-launch blocking download of the `.tflite` model and labels `.txt` file before allowing access to the camera.

1. **Step 1: Network Dependencies**
   - Add Ktor Client dependencies (core, cio or android, content-negotiation, serialization-json) to `app/build.gradle.kts` for downloading files.
   - Add Coroutines `Dispatchers.IO` usage for file writing.

2. **Step 2: Model Downloader Service (Resilient)**
   - Create `ModelDownloadService` in `data/remote/` to download the model and labels files.
   - Use a `.tmp` extension during download and rename to `.tflite` only upon successful completion to prevent corrupted files if interrupted.
   - Run the download in an Application-scoped coroutine or `DownloadManager` to survive configuration changes.
   - Create a SharedPreferences or DataStore flag to track download completion.

3. **Step 3: Blocking Loading Screen (Compose)**
   - Create a `LoadingScreen` composable in `presentation/loading/` that checks the download flag.
   - If not downloaded, show a progress bar and trigger the download via a `LoadingViewModel`.
   - On success, navigate to the main camera screen.

## Plan 02-02: CameraX Setup and Capture Flow
**Objective:** Set up the live camera preview in Compose and the manual capture -> review -> save flow.

1. **Step 1: CameraX Dependencies**
   - Add CameraX dependencies (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`) to `build.gradle.kts`.
   - Ensure camera permissions are declared in `AndroidManifest.xml` and handled in Compose.

2. **Step 2: Live Preview Composable**
   - Create `CameraScreen` in `presentation/camera/` using an `AndroidView` wrapping `PreviewView`.
   - Bind the camera lifecycle to the current `LifecycleOwner`.

3. **Step 3: Capture and Review Flow**
   - Add a manual shutter button over the preview.
   - Upon tap, capture the image using `ImageCapture.takePicture(outputFileOptions, executor, callback)` to save the image directly to `context.filesDir`.
   - Create a `ReviewScreen` that displays the saved image from the file path. Provide "Save" and "Retake" buttons.
   - On "Save", map the data (including the generated absolute file path) to a `Capture` and call the existing `AddCaptureUseCase` to save to Room.
   - On "Retake", delete the captured file and return to the live preview.

## Plan 02-03: ML Kit Custom Model Integration
**Objective:** Run the `.tflite` model on live camera frames and on the captured image.

1. **Step 1: ML Kit Dependencies**
   - Add `com.google.mlkit:image-labeling-custom` to `build.gradle.kts`.

2. **Step 2: Image Analyzer**
   - Implement `ImageAnalysis.Analyzer` in a `SpeciesAnalyzer` class.
   - Load the custom `LocalModel` from the downloaded `.tflite` file in `filesDir`.
   - In `analyze(imageProxy: ImageProxy)`, convert the proxy to an `InputImage` using `InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)` to handle rotation.
   - Process the `InputImage`, applying frame-dropping using `STRATEGY_KEEP_ONLY_LATEST` on the ImageAnalysis use case to avoid blocking.

3. **Step 3: Live UI Updates**
   - Read the downloaded `.txt` file to map model output indices to scientific names.
   - Pass the top-1 recognized label back to the `CameraScreen` via ViewModel StateFlow.
   - Display the identified species name and confidence overlay on the live preview.

4. **Step 4: Post-Capture Details**
   - On the `ReviewScreen`, pass the captured image to the labeler again.
   - Display ONLY the Top-1 result prominently (e.g. "The capture") by default.
   - Add a tiny "info" or "details" button in the corner of the UI.
   - Only when tapped, expand a "Details Section" showing the top 3 results, their confidence percentages, and a "Report wrong match" placeholder button for manual corrections.

## Verification
- Verify the app blocks on first launch until models are downloaded.
- Verify CameraX preview renders smoothly and asks for permissions.
- Verify ML Kit successfully labels frames without crashing/OOM.
- Verify the capture -> review -> room database flow persists the image path and species data correctly.

## Desviaciones del Plan y Decisiones de Implementación (Lecciones Aprendidas)

Durante el desarrollo de esta fase, se realizaron varios ajustes críticos al plan original para solucionar problemas técnicos del modelo y del sistema operativo:

1. **Migración a TensorFlow Lite Nativo (`org.tensorflow:tensorflow-lite`)**:
   - *Desviación*: Se descartó la librería `com.google.mlkit:image-labeling-custom` planificada en la sección 02-03.
   - *Razón*: El modelo `species_model.tflite` de iNaturalist carece de los metadatos de normalización obligatorios de ML Kit (`NormalizationOptions`), lo que provocaba un fallo de inicialización inmediato (`ClassifierClientCalculator failed`).
   - *Solución*: Se implementó el preprocesamiento manual (redimensionamiento a `299x299` y normalización de píxeles a `[-1.0f, 1.0f]`) y la ejecución manual del intérprete de TFLite en [SpeciesAnalyzer.kt](file:///C:/Users/camil/Documents/antigravity/kind-kepler/app/src/main/java/com/patagonia/app/data/local/SpeciesAnalyzer.kt).

2. **Sincronización (Lock) en el Intérprete**:
   - *Desviación*: El intérprete nativo de TFLite no es seguro para hilos (`not thread-safe`).
   - *Razón*: Al analizar imágenes concurrentemente desde la cámara en vivo (hilo del ImageAnalysis de CameraX) y desde la galería (hilo del coroutine `Dispatchers.Default`), se generaba una condición de carrera que corrompía las predicciones.
   - *Solución*: Se añadió sincronización explícita (`synchronized(interpreterLock)`) en la llamada a `runInference()`.

3. **Modelo en `assets/` y Omisión del Descargador**:
   - *Desviación*: Se priorizó incluir los archivos `species_model.tflite` (94MB) y `species_labels.txt` (24,933 clases) directamente en la carpeta `assets/` en lugar de requerir una descarga inicial por internet (Plan 02-01).
   - *Solución*: Se modificaron `KtorModelDownloader.kt` y `LoadingViewModel.kt` para detectar la presencia local de los assets y omitir la descarga, garantizando un funcionamiento 100% offline-first.

4. **Traducción de Taxonomía y Umbrales**:
   - *Desviación*: Se descartó el umbral estricto del 15% para no clasificar como "Especie Desconocida" (debido a la gran cantidad de clases, las confianzas se diluyen).
   - *Solución*: Se muestran siempre las Top 3 predicciones y se añadió una capa de mapeo ([SpeciesMapping.kt](file:///C:/Users/camil/Documents/antigravity/kind-kepler/app/src/main/java/com/patagonia/app/data/local/SpeciesMapping.kt)) para traducir los nombres científicos a nombres comunes chilenos en español.

5. **Corrección del Ciclo de Vida de CameraX**:
   - *Desviación*: Vinculación de `ProcessCameraProvider` movida al bloque `factory` en lugar de `update` de Compose `AndroidView`.
   - *Razón*: Evita re-vincular la cámara en cada recomposición del UI, eliminando el error de Binder Timeout (`TimeoutException` de 5000 ms).

6. **Rotación EXIF en Galería**:
   - *Solución*: Se implementó la lectura de la orientación EXIF de las imágenes de la galería para rotar los mapas de bits antes del análisis, evitando que imágenes en vertical se envíen inclinadas al modelo.

