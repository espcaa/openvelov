package fish.alice.openvelov.ui.maps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import fish.alice.openvelov.data.remote.StationDto
import org.json.JSONArray
import org.json.JSONObject

fun List<StationDto>.toGeoJson(): String {

    val features = JSONArray()

    for (station in this) {
        val lat = station.location.latitude
        val lon = station.location.longitude

        if (lat == 0.0 && lon == 0.0) continue
        if (lat.isNaN() || lon.isNaN() || lat.isInfinite() || lon.isInfinite()) continue

        val totalBikes = station.availabilities.main.bikes.electrical +
                station.availabilities.main.bikes.mechanical

        val geometry = JSONObject().apply {
            put("type", "Point")
            put("coordinates", JSONArray().apply {
                put(lon)
                put(lat)
            })
        }

        val properties = JSONObject().apply {
            put("id", station.id)
            put("label", totalBikes.toString())
            put("bikes", totalBikes)
        }

        val feature = JSONObject().apply {
            put("type", "Feature")
            put("geometry", geometry)
            put("properties", properties)
        }

        features.put(feature)
    }

    val featureCollection = JSONObject().apply {
        put("type", "FeatureCollection")
        put("features", features)
    }

    return featureCollection.toString()
}

private fun Color.toHex(): String = String.format("#%06X", 0xFFFFFF and this.toArgb())