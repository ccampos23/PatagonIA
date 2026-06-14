package com.patagonia.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.patagonia.app.data.local.dao.CaptureDao
import com.patagonia.app.data.local.entity.CaptureEntity

@Database(
    entities = [CaptureEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PatagoniaDatabase : RoomDatabase() {
    abstract fun captureDao(): CaptureDao
}
