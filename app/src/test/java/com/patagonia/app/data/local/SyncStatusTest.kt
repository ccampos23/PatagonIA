package com.patagonia.app.data.local

import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.data.local.converter.SyncStatusConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SyncStatusTest {

    @Test
    fun testSyncStatusEnumValuesExist() {
        val values = SyncStatus.values().map { it.name }
        val expected = listOf("PENDING_INSERT", "PENDING_UPDATE", "SYNCED", "PENDING_DELETE", "SYNC_FAILED")
        for (name in expected) {
            assert(values.contains(name)) { "SyncStatus is missing $name" }
        }
    }

    @Test
    fun testSyncStatusConverter() {
        val converter = SyncStatusConverter()
        assertEquals("PENDING_INSERT", converter.toString(SyncStatus.PENDING_INSERT))
        assertEquals(SyncStatus.PENDING_INSERT, converter.toSyncStatus("PENDING_INSERT"))
    }
}
