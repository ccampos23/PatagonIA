package com.patagonia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.patagonia.app.data.local.entity.CaptureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaptureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapture(capture: CaptureEntity)

    @Update
    suspend fun updateCapture(capture: CaptureEntity)

    @Delete
    suspend fun deleteCapture(capture: CaptureEntity)

    @Query("SELECT * FROM captures WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllCaptures(): Flow<List<CaptureEntity>>

    @Query("SELECT * FROM captures WHERE id = :id")
    suspend fun getCaptureById(id: String): CaptureEntity?

    @Query("SELECT * FROM captures WHERE syncStatus != 'SYNCED'")
    fun getPendingSyncCaptures(): Flow<List<CaptureEntity>>

    @Query("UPDATE captures SET syncStatus = 'SYNCED', remoteId = :remoteId WHERE id = :id")
    suspend fun markAsSynced(id: String, remoteId: String)

    @Query("UPDATE captures SET syncStatus = 'SYNC_FAILED' WHERE id = :id")
    suspend fun markAsSyncFailed(id: String)

    @Query("SELECT * FROM captures WHERE remoteId = :remoteId")
    suspend fun getByRemoteId(remoteId: String): CaptureEntity?

    @Query("UPDATE captures SET isDeleted = 1, syncStatus = 'PENDING_DELETE' WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("SELECT COUNT(*) FROM captures WHERE isDeleted = 0")
    fun getCaptureCount(): Flow<Int>
}
