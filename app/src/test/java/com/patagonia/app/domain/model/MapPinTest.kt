package com.patagonia.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import com.patagonia.app.domain.model.SyncStatus

class MapPinTest {

    @Test
    fun `fromCapture maps capture fields correctly`() {
        val capture = Capture(
            id = "cap-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "/img/puma.jpg",
            latitude = -51.0,
            longitude = -73.0,
            altitude = 500.0,
            confidence = 0.95f,
            notes = "Spotted near lake",
            syncStatus = SyncStatus.PENDING_INSERT
        )

        val pin = MapPin.fromCapture(capture)

        assertEquals("cap-1", pin.captureId)
        assertEquals("Puma", pin.speciesName)
        assertEquals(-51.0, pin.latitude, 0.001)
        assertEquals(-73.0, pin.longitude, 0.001)
        assertEquals(0.95f, pin.confidence!!, 0.01f)
        assertEquals("/img/puma.jpg", pin.imagePath)
    }

    @Test
    fun `fromCapture infers MAMMAL category for puma`() {
        val capture = Capture(
            id = "1", speciesName = "Puma", scientificName = null,
            timestamp = 0L, imagePath = "", latitude = 0.0,
            longitude = 0.0, altitude = null, confidence = null,
            notes = null, syncStatus = SyncStatus.PENDING_INSERT
        )
        assertEquals(SpeciesCategory.MAMMAL, MapPin.fromCapture(capture).category)
    }

    @Test
    fun `fromCapture infers BIRD category for condor`() {
        val capture = Capture(
            id = "2", speciesName = "Andean Condor", scientificName = null,
            timestamp = 0L, imagePath = "", latitude = 0.0,
            longitude = 0.0, altitude = null, confidence = null,
            notes = null, syncStatus = SyncStatus.PENDING_INSERT
        )
        assertEquals(SpeciesCategory.BIRD, MapPin.fromCapture(capture).category)
    }
}
