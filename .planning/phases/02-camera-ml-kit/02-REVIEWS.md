# Phase 2 Plan Reviews

**Reviewer:** Codex CLI (Default)
**Date:** 2026-06-14T18:05:00-04:00

## Concerns

### 🔴 HIGH: Fragile Model Download (Plan 02-01)
Downloading large `.tflite` model files using a simple Ktor call in a ViewModel/Repository is extremely fragile. If the user backgrounds the app or the device goes to sleep during the download, the process may be killed, resulting in a corrupted model file.
**Recommendation:** Use Android's built-in `DownloadManager` or `WorkManager` for the download, or download to a `.tmp` file and rename it only upon successful completion, verifying the file integrity before marking it as downloaded.

### 🔴 HIGH: Missing Image File Persistence (Plan 02-02)
Step 3 states "capture the image using `ImageCapture.takePicture`" and then "call `AddCaptureUseCase` to save to Room". The `CaptureEntity` expects an `imagePath: String`. The plan misses the crucial step of actually saving the captured `ImageProxy` or `Bitmap` to device storage (e.g., `context.filesDir`) to generate this path. Room cannot store the image itself.
**Recommendation:** Explicitly add a step to save the captured photo to local storage (app-specific files directory) and pass that resulting absolute path to the UseCase.

### 🟡 MEDIUM: ImageProxy to InputImage Conversion (Plan 02-03)
The ML Kit `CustomImageLabeler` cannot process an `ImageProxy` directly. It requires an `InputImage`.
**Recommendation:** Add a step to convert `ImageProxy` to `InputImage`, ensuring that the `imageInfo.rotationDegrees` is passed correctly so the model doesn't process sideways images.
