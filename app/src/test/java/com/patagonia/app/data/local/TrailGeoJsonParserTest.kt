package com.patagonia.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class TrailGeoJsonParserTest {

    private val sampleGeoJson = """
    {
      "type": "FeatureCollection",
      "features": [
        {
          "type": "Feature",
          "properties": {
            "name": "Sendero Base Torres",
            "difficulty": "Difficult",
            "id": "trail-base-torres"
          },
          "geometry": {
            "type": "LineString",
            "coordinates": [
              [-72.9544, -50.9422],
              [-72.9510, -50.9395],
              [-72.9455, -50.9370]
            ]
          }
        },
        {
          "type": "Feature",
          "properties": {
            "name": "Sendero Lago Grey",
            "difficulty": "Easy",
            "id": "trail-lago-grey"
          },
          "geometry": {
            "type": "LineString",
            "coordinates": [
              [-73.1100, -51.0200],
              [-73.1060, -51.0170]
            ]
          }
        }
      ]
    }
    """.trimIndent()

    @Test
    fun `parse returns correct number of trails`() {
        val trails = TrailGeoJsonParser.parse(sampleGeoJson)
        assertEquals(2, trails.size)
    }

    @Test
    fun `parse extracts trail name from properties`() {
        val trails = TrailGeoJsonParser.parse(sampleGeoJson)
        assertEquals("Sendero Base Torres", trails[0].name)
        assertEquals("Sendero Lago Grey", trails[1].name)
    }

    @Test
    fun `parse extracts trail difficulty from properties`() {
        val trails = TrailGeoJsonParser.parse(sampleGeoJson)
        assertEquals("Difficult", trails[0].difficulty)
        assertEquals("Easy", trails[1].difficulty)
    }

    @Test
    fun `parse extracts trail id from properties`() {
        val trails = TrailGeoJsonParser.parse(sampleGeoJson)
        assertEquals("trail-base-torres", trails[0].id)
        assertEquals("trail-lago-grey", trails[1].id)
    }

    @Test
    fun `parse extracts coordinates as lng-lat pairs`() {
        val trails = TrailGeoJsonParser.parse(sampleGeoJson)
        val coords = trails[0].coordinates
        assertEquals(3, coords.size)
        assertEquals(-72.9544, coords[0].first, 0.0001)
        assertEquals(-50.9422, coords[0].second, 0.0001)
    }

    @Test
    fun `parse handles empty feature collection`() {
        val emptyGeoJson = """{"type":"FeatureCollection","features":[]}"""
        val trails = TrailGeoJsonParser.parse(emptyGeoJson)
        assertEquals(0, trails.size)
    }

    @Test
    fun `parse skips non-LineString geometry types`() {
        val mixedGeoJson = """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "properties": {"name": "A Point", "id": "point-1"},
              "geometry": {"type": "Point", "coordinates": [-72.0, -51.0]}
            },
            {
              "type": "Feature",
              "properties": {"name": "A Trail", "difficulty": "Easy", "id": "trail-1"},
              "geometry": {"type": "LineString", "coordinates": [[-72.0, -51.0], [-72.1, -51.1]]}
            }
          ]
        }
        """.trimIndent()
        val trails = TrailGeoJsonParser.parse(mixedGeoJson)
        assertEquals(1, trails.size)
        assertEquals("A Trail", trails[0].name)
    }
}
