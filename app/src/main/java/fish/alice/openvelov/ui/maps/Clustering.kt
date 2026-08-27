package fish.alice.openvelov.ui.maps

import fish.alice.openvelov.data.remote.StationDto
import org.maplibre.spatialk.geojson.Position
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow


sealed interface MapMarkerUi {
    val id: String
    val position: Position

    data class SingleStation(
        override val id: String,
        override val position: Position,
        val electricalBikes: Int,
        val mechanicalBikes: Int,
        val stands: Int,
    ) : MapMarkerUi

    data class Cluster(
        override val id: String,
        override val position: Position,
        val count: Int,
    ) : MapMarkerUi
}

object MapClustering {
    fun cluster(
        stations: List<StationDto>,
        zoom: Double,
        gridSizeDp: Double = 60.0,
    ): List<MapMarkerUi> {
        if (stations.isEmpty()) return emptyList()

        // high zoom, show everything
        if (zoom > 15.0) {
            return stations.map { station ->
                MapMarkerUi.SingleStation(
                    id = station.number.toString(),
                    position = Position(station.location.longitude, station.location.latitude),
                    electricalBikes = station.availabilities.main.bikes.electrical,
                    mechanicalBikes = station.availabilities.main.bikes.mechanical,
                    stands = station.availabilities.main.stands
                )
            }
        }

        val scale = 2.0.pow(zoom) * 256.0
        val cellSizeDeg = (gridSizeDp * 360.0) / scale

        val grid = mutableMapOf<Pair<Long, Long>, MutableList<StationDto>>()

        for (station in stations) {
            val latRad = Math.toRadians(station.location.latitude)
            val lonCell = floor(station.location.longitude / (cellSizeDeg / cos(latRad))).toLong()
            val latCell = floor(station.location.latitude / cellSizeDeg).toLong()

            grid.getOrPut(lonCell to latCell) { mutableListOf() }.add(station)
        }

        return grid.map { (key, group) ->
            if (group.size == 1) {
                val s = group.first()
                MapMarkerUi.SingleStation(
                    id = s.id,
                    position = Position(s.location.longitude, s.location.latitude),
                    electricalBikes = s.availabilities.main.bikes.electrical,
                    mechanicalBikes = s.availabilities.main.bikes.mechanical,
                    stands = s.availabilities.main.stands
                )
            } else {
                val avgLat = group.map { it.location.latitude }.average()
                val avgLng = group.map { it.location.longitude }.average()

                MapMarkerUi.Cluster(
                    id = "cluster_${key.first}_${key.second}",
                    position = Position(avgLng, avgLat),
                    count = group.size,
                )
            }
        }
    }
}