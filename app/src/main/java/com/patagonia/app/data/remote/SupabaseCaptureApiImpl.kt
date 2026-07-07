package com.patagonia.app.data.remote

import com.patagonia.app.data.remote.dto.RemoteCaptureDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseCaptureApiImpl @Inject constructor(
    private val postgrest: Postgrest
) : SupabaseCaptureApi {

    private val table = postgrest["captures"]

    override suspend fun insertCapture(dto: RemoteCaptureDto): String {
        val result = table.insert(dto) {
            select()
        }
        return result.decodeSingle<RemoteCaptureDto>().id
    }

    override suspend fun updateCapture(dto: RemoteCaptureDto) {
        table.update(dto) {
            filter {
                eq("id", dto.id)
            }
        }
    }

    override suspend fun softDeleteCapture(remoteId: String) {
        table.update(buildJsonObject {
            put("is_deleted", true)
        }) {
            filter {
                eq("id", remoteId)
            }
        }
    }

    override suspend fun fetchAllCaptures(userId: String): List<RemoteCaptureDto> {
        val result = table.select {
            filter {
                eq("user_id", userId)
            }
        }
        return result.decodeList()
    }

    override suspend fun updateSharingStatus(remoteId: String, isShared: Boolean) {
        table.update(buildJsonObject {
            put("is_shared", isShared)
        }) {
            filter {
                eq("id", remoteId)
            }
        }
    }
}
