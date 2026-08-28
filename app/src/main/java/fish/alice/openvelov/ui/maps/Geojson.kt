package fish.alice.openvelov.ui.maps

import fish.alice.openvelov.data.remote.StationDto
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

fun List<StationDto>.toGeoJsonData(): GeoJsonData {
    val features = this.mapNotNull { station ->
        val lat = station.location.latitude
        val lon = station.location.longitude

        if (lat == 0.0 && lon == 0.0) return@mapNotNull null
        if (lat.isNaN() || lon.isNaN() || lat.isInfinite() || lon.isInfinite()) return@mapNotNull null

        val totalBikes = station.availabilities.main.bikes.electrical +
                station.availabilities.main.bikes.mechanical

        Feature(
            geometry = Point(
                coordinates = Position(
                    longitude = lon,
                    latitude = lat
                )
            ),
            properties = buildJsonObject {
                put("id", station.id)
                put("label", totalBikes.toString())
                put("bikes", totalBikes)
            }
        )
    }

    return GeoJsonData.Features(FeatureCollection(features))
}