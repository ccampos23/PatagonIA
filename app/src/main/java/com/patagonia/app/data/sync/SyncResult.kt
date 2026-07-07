package com.patagonia.app.data.sync

data class SyncError(val captureId: String, val message: String, val error: Throwable)

sealed class SyncResult {
    data class Success(val pushed: Int, val pulled: Int, val failed: Int = 0) : SyncResult()
    data class PartialSuccess(
        val pushed: Int,
        val pulled: Int,
        val failed: Int,
        val errors: List<SyncError>
    ) : SyncResult()
    data class Failure(val error: Throwable) : SyncResult()
}
