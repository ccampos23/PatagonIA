package com.patagonia.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.patagonia.app.data.local.dao.CaptureDao
import com.patagonia.app.data.local.entity.CaptureEntity
import com.patagonia.app.domain.model.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class CaptureDaoTest {

    private lateinit var db: PatagoniaDatabase
    private lateinit var captureDao: CaptureDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PatagoniaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        captureDao = db.captureDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetCapture() = runTest {
        val capture = CaptureEntity(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "/path/to/image",
            latitude = -50.0,
            longitude = -73.0,
            altitude = 100.0,
            confidence = 0.95f,
            notes = "Spotted near the base towers",
            syncStatus = SyncStatus.PENDING_INSERT,
            remoteId = null,
            isShared = false,
            isDeleted = false,
            userId = "user-123"
        )
        captureDao.insertCapture(capture)
        val retrieved = captureDao.getCaptureById("test-1")
        assertNotNull(retrieved)
        assertEquals("Puma", retrieved?.speciesName)
        assertEquals(SyncStatus.PENDING_INSERT, retrieved?.syncStatus)
        assertEquals("user-123", retrieved?.userId)
    }

    @Test
    fun testGetPendingSyncCaptures() = runTest {
        val c1 = CaptureEntity(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "",
            latitude = -50.0,
            longitude = -73.0,
            altitude = 100.0,
            confidence = 0.95f,
            notes = "",
            syncStatus = SyncStatus.PENDING_INSERT
        )
        val c2 = CaptureEntity(
            id = "test-2",
            speciesName = "Huemul",
            scientificName = "Hippocamelus bisulcus",
            timestamp = 1001L,
            imagePath = "",
            latitude = -50.0,
            longitude = -73.0,
            altitude = 100.0,
            confidence = 0.95f,
            notes = "",
            syncStatus = SyncStatus.SYNCED
        )
        val c3 = CaptureEntity(
            id = "test-3",
            speciesName = "Condor",
            scientificName = "Vultur gryphus",
            timestamp = 1002L,
            imagePath = "",
            latitude = -50.0,
            longitude = -73.0,
            altitude = 100.0,
            confidence = 0.95f,
            notes = "",
            syncStatus = SyncStatus.PENDING_UPDATE
        )

        captureDao.insertCapture(c1)
        captureDao.insertCapture(c2)
        captureDao.insertCapture(c3)

        val pending = captureDao.getPendingSyncCaptures().first()
        assertEquals(2, pending.size)
        val ids = pending.map { it.id }
        assert(ids.contains("test-1"))
        assert(ids.contains("test-3"))
    }

    @Test
    fun testMarkAsSynced() = runTest {
        val capture = CaptureEntity(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "",
            latitude = -50.0,
            longitude = -73.0,
            altitude = 100.0,
            confidence = 0.95f,
            notes = "",
            syncStatus = SyncStatus.PENDING_INSERT
        )
        captureDao.insertCapture(capture)
        captureDao.markAsSynced("test-1", "remote-uuid-abc")

        val retrieved = captureDao.getCaptureById("test-1")
        assertEquals(SyncStatus.SYNCED, retrieved?.syncStatus)
        assertEquals("remote-uuid-abc", retrieved?.remoteId)
    }

    @Test
    fun testMarkAsSyncFailed() = runTest {
        val capture = CaptureEntity(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "",
            latitude = -50.0,
            longitude = -73.0,
            altitude = 100.0,
            confidence = 0.95f,
            notes = "",
            syncStatus = SyncStatus.PENDING_INSERT
        )
        captureDao.insertCapture(capture)
        captureDao.markAsSyncFailed("test-1")

        val retrieved = captureDao.getCaptureById("test-1")
        assertEquals(SyncStatus.SYNC_FAILED, retrieved?.syncStatus)
    }

    @Test
    fun testGetByRemoteId() = runTest {
        val capture = CaptureEntity(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "",
            latitude = -50.0,
            longitude = -73.0,
            altitude = 100.0,
            confidence = 0.95f,
            notes = "",
            syncStatus = SyncStatus.SYNCED,
            remoteId = "remote-uuid-abc"
        )
        captureDao.insertCapture(capture)

        val retrieved = captureDao.getByRemoteId("remote-uuid-abc")
        assertNotNull(retrieved)
        assertEquals("test-1", retrieved?.id)
    }

    @Test
    fun testSoftDelete() = runTest {
        val capture = CaptureEntity(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "",
            latitude = -50.0,
            longitude = -73.0,
            altitude = 100.0,
            confidence = 0.95f,
            notes = "",
            syncStatus = SyncStatus.SYNCED
        )
        captureDao.insertCapture(capture)
        captureDao.softDelete("test-1")

        val retrieved = captureDao.getCaptureById("test-1")
        assertEquals(true, retrieved?.isDeleted)
        assertEquals(SyncStatus.PENDING_DELETE, retrieved?.syncStatus)
    }

    @Test
    fun testRoomMigration1To2() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbPath = context.getDatabasePath("test-migration.db")
        if (dbPath.exists()) {
            dbPath.delete()
        }

        // 1. Create v1 database structure using raw SQLite
        val helper = androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("test-migration.db")
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL("""
                            CREATE TABLE IF NOT EXISTS `captures` (
                                `id` TEXT NOT NULL, 
                                `speciesName` TEXT NOT NULL, 
                                `scientificName` TEXT, 
                                `timestamp` INTEGER NOT NULL, 
                                `imagePath` TEXT NOT NULL, 
                                `latitude` REAL NOT NULL, 
                                `longitude` REAL NOT NULL, 
                                `altitude` REAL, 
                                `confidence` REAL, 
                                `notes` TEXT, 
                                `isSynced` INTEGER NOT NULL DEFAULT 0, 
                                PRIMARY KEY(`id`)
                            )
                        """.trimIndent())
                    }
                    override fun onUpgrade(db: androidx.sqlite.db.SupportSQLiteDatabase, oldV: Int, newV: Int) {}
                })
                .build()
        )
        val v1Db = helper.writableDatabase
        
        // 2. Insert test data into v1 DB
        v1Db.execSQL("INSERT INTO captures (id, speciesName, timestamp, imagePath, latitude, longitude, isSynced) VALUES ('mig-1', 'Puma', 1000, '/img', -50.0, -73.0, 1)")
        v1Db.execSQL("INSERT INTO captures (id, speciesName, timestamp, imagePath, latitude, longitude, isSynced) VALUES ('mig-2', 'Huemul', 1001, '/img', -50.0, -73.0, 0)")
        v1Db.close()

        // 3. Open database with Room at version 2 and apply migration
        val migratedDb = Room.databaseBuilder(context, PatagoniaDatabase::class.java, "test-migration.db")
            .addMigrations(PatagoniaDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()

        // 4. Verify data is preserved and syncStatus is correctly migrated
        val c1 = migratedDb.captureDao().getCaptureById("mig-1")
        assertNotNull(c1)
        assertEquals("Puma", c1?.speciesName)
        assertEquals(SyncStatus.SYNCED, c1?.syncStatus)
        assertEquals(false, c1?.isDeleted)
        assertEquals(false, c1?.isShared)
        assertNull(c1?.remoteId)
        assertNull(c1?.userId)

        val c2 = migratedDb.captureDao().getCaptureById("mig-2")
        assertNotNull(c2)
        assertEquals("Huemul", c2?.speciesName)
        assertEquals(SyncStatus.PENDING_INSERT, c2?.syncStatus)
        assertEquals(false, c2?.isDeleted)
        assertEquals(false, c2?.isShared)
        assertNull(c2?.remoteId)
        assertNull(c2?.userId)

        migratedDb.close()
    }
}
