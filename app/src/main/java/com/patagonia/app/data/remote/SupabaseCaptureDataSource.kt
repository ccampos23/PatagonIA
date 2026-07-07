package com.patagonia.app.data.remote

import com.patagonia.app.data.remote.dto.RemoteCaptureDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for remote captures stored in Supabase.
 *
 * Supabase Schema Reference (D-20):
 * ```sql
 * CREATE TABLE captures (
 *   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 *   user_id UUID REFERENCES auth.users(id) NOT NULL,
 *   species_name TEXT NOT NULL,
 *   image_path TEXT NOT NULL,
 *   latitude DOUBLE PRECISION NOT NULL,
 *   longitude DOUBLE PRECISION NOT NULL,
 *   captured_at BIGINT NOT NULL,
 *   confidence REAL NOT NULL,
 *   notes TEXT DEFAULT '',
 *   is_shared BOOLEAN DEFAULT false,
 *   is_deleted BOOLEAN DEFAULT false,
 *   created_at TIMESTAMPTZ DEFAULT now(),
 *   updated_at TIMESTAMPTZ DEFAULT now()
 * );
 * ALTER TABLE captures ENABLE ROW LEVEL SECURITY;
 * CREATE POLICY "Users can CRUD own captures" ON captures
 *   FOR ALL TO authenticated
 *   USING ((select auth.uid()) = user_id)
 *   WITH CHECK ((select auth.uid()) = user_id);
 * ```
 */
@Singleton
class SupabaseCaptureDataSource @Inject constructor(
    private val api: SupabaseCaptureApi
) {
    suspend fun insertCapture(dto: RemoteCaptureDto): String = api.insertCapture(dto)
    suspend fun updateCapture(dto: RemoteCaptureDto) = api.updateCapture(dto)
    suspend fun softDeleteCapture(remoteId: String) = api.softDeleteCapture(remoteId)
    suspend fun fetchAllCaptures(userId: String): List<RemoteCaptureDto> = api.fetchAllCaptures(userId)
    suspend fun updateSharingStatus(remoteId: String, isShared: Boolean) = api.updateSharingStatus(remoteId, isShared)
}
