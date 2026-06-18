package com.patagonia.app.data.local

import com.patagonia.app.domain.model.Trail
import org.json.JSONObject

/**
 * Parses GeoJSON FeatureCollection strings into Trail domain models.
 * Used to load pre-packaged Chilean trail data from app assets (D-13).
 */
object TrailGeoJsonParser {

    /**
     * Parse a GeoJSON FeatureCollection string into a list of Trail models.
     *
     * Expected GeoJSON structure:
     * ```json
     * {
     *   "type": "FeatureCollection",
     *   "features": [{
     *     "properties": { "name": "...", "difficulty": "...", "id": "..." },
     *     "geometry": { "type": "LineString", "coordinates": [[lng, lat], ...] }
     *   }]
     * }
     * ```
     */
    fun parse(geoJsonString: String): List<Trail> {
        val json = JSONObject(geoJsonString)
        val features = json.getJSONArray("features")
        val trails = mutableListOf<Trail>()

        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val properties = feature.getJSONObject("properties")
            val geometry = feature.getJSONObject("geometry")

            // Only handle LineString geometries for trails
            if (geometry.getString("type") != "LineString") continue

            val coordsArray = geometry.getJSONArray("coordinates")
            val coordinates = mutableListOf<Pair<Double, Double>>()

            for (j in 0 until coordsArray.length()) {
                val coord = coordsArray.getJSONArray(j)
                val lng = coord.getDouble(0)
                val lat = coord.getDouble(1)
                coordinates.add(lng to lat)
            }

            trails.add(
                Trail(
                    id = properties.optString("id", "trail-$i"),
                    name = properties.optString("name", "Unknown Trail"),
                    difficulty = properties.optString("difficulty", "Unknown"),
                    coordinates = coordinates
                )
            )
        }

        return trails
    }
}
