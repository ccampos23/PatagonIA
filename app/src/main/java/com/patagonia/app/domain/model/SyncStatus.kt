package com.patagonia.app.domain.model

enum class SyncStatus {
    PENDING_INSERT,
    PENDING_UPDATE,
    SYNCED,
    PENDING_DELETE,
    SYNC_FAILED
}
