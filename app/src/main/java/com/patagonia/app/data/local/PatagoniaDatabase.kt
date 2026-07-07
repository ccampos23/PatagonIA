package com.patagonia.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patagonia.app.data.local.converter.SyncStatusConverter
import com.patagonia.app.data.local.dao.CaptureDao
import com.patagonia.app.data.local.entity.CaptureEntity

@Database(
    entities = [CaptureEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(SyncStatusConverter::class)
abstract class PatagoniaDatabase : RoomDatabase() {
    abstract fun captureDao(): CaptureDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE captures RENAME TO captures_old")
                db.execSQL("""
                    CREATE TABLE captures (
                        id TEXT PRIMARY KEY NOT NULL,
                        speciesName TEXT NOT NULL,
                        scientificName TEXT,
                        timestamp INTEGER NOT NULL,
                        imagePath TEXT NOT NULL,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        altitude REAL,
                        confidence REAL,
                        notes TEXT,
                        syncStatus TEXT NOT NULL DEFAULT 'SYNCED',
                        remoteId TEXT,
                        isShared INTEGER NOT NULL DEFAULT 0,
                        isDeleted INTEGER NOT NULL DEFAULT 0,
                        userId TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO captures (
                        id, speciesName, scientificName, timestamp, imagePath,
                        latitude, longitude, altitude, confidence, notes,
                        syncStatus, remoteId, isShared, isDeleted, userId
                    )
                    SELECT 
                        id, speciesName, scientificName, timestamp, imagePath,
                        latitude, longitude, altitude, confidence, notes,
                        CASE WHEN isSynced = 1 THEN 'SYNCED' ELSE 'PENDING_INSERT' END,
                        NULL, 0, 0, NULL
                    FROM captures_old
                """.trimIndent())
                db.execSQL("DROP TABLE captures_old")
            }
        }
    }
}
