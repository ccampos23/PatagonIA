package com.patagonia.app.data.local.converter

import androidx.room.TypeConverter
import com.patagonia.app.domain.model.SyncStatus

class SyncStatusConverter {

    @TypeConverter
    fun toString(status: SyncStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return try {
            SyncStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            SyncStatus.PENDING_INSERT
        }
    }
}
