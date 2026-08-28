package fish.alice.openvelov.ui.maps

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fish.alice.openvelov.data.remote.StationDto
import fish.alice.openvelov.data.remote.StationsApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.Feature.get
import org.maplibre.compose.expressions.dsl.Feature.has
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.coalesce
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.convertToNumber
import org.maplibre.compose.expressions.dsl.convertToString
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.not
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.expressions.dsl.step
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import javax.inject.Inject

private const val DEFAULT_LATITUDE = 45.750000
private const val DEFAULT_LONGITUDE = 4.850000
private const val DEFAULT_ZOOM = 12.0

private const val OPENFREEMAP_LIBERTY = "https://tiles.openfreemap.org/styles/liberty"

@HiltViewModel
class MapViewModel @Inject constructor(
    private val stationsApi: StationsApi,
) : ViewModel() {
    var isMapLoaded by mutableStateOf(false)
        private set

    private val _rawStations = MutableStateFlow<List<StationDto>>(emptyList())
    val rawStations: StateFlow<List<StationDto>> = _rawStations.asStateFlow()

    private val _cameraPosition = MutableStateFlow(
        CameraPosition(
            target = Position(DEFAULT_LONGITUDE, DEFAULT_LATITUDE),
            zoom = DEFAULT_ZOOM
        )
    )
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()

    fun onCameraMoved(newPosition: CameraPosition) {
        _cameraPosition.value = newPosition
    }

    fun loadStations() {
        if (isMapLoaded) return
        viewModelScope.launch {
            try {
                Log.d("MapViewModel", "Loading stations from API...")
                _rawStations.value = stationsApi.stations()
                println("Loaded ${_rawStations.value.size} stations")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MapViewModel", "Failed to load stations", e)
            } finally {
                isMapLoaded = true
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MapLibreView(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val stations by viewModel.rawStations.collectAsStateWithLifecycle()

    val camera = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(DEFAULT_LONGITUDE, DEFAULT_LATITUDE),
            zoom = DEFAULT_ZOOM
        )
    )

    LaunchedEffect(camera) {
        snapshotFlow { camera.position }
            .distinctUntilChanged()
            .collect { viewModel.onCameraMoved(it) }
    }

    val geoJsonString = remember(stations) {
        stations.toGeoJson()
    }

    Box(modifier = modifier.fillMaxSize()) {
        MaplibreMap(
            baseStyle = BaseStyle.Uri(OPENFREEMAP_LIBERTY),
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            overlay = MapOverlay.None,
            onMapLoadFinished = {
                viewModel.loadStations()
            },
        ) {
            val stationsSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(geoJsonString),
                options = GeoJsonOptions(cluster = true,clusterRadius = 50,clusterMaxZoom = 14, synchronousUpdate = true)
            )

            LaunchedEffect(geoJsonString) {
                Log.d(
                    "MapLibreView",
                    "geoJson len=${geoJsonString.length} stations=${stations.size}"
                )
            }

            CircleLayer(
                id = "clusters",
                source = stationsSource,
                filter = has("point_count"),
                radius = step(
                    get("point_count").convertToNumber(),
                    const(16.dp),
                    25.0 to const(22.dp),
                    100.0 to const(30.dp),
                ),
                color = const(MaterialTheme.colorScheme.onBackground),
                strokeColor = const(MaterialTheme.colorScheme.background),
                strokeWidth = const(2.dp)
            )

            SymbolLayer(
                id = "cluster-count",
                source = stationsSource,
                filter = has("point_count"),
                textField = format(span(get("point_count_abbreviated").convertToString())),
                textFont = const(listOf("Noto Sans Bold")),
                textColor = const(MaterialTheme.colorScheme.background),
                textSize = const(13.sp),
                textAllowOverlap = const(true),
                textIgnorePlacement = const(true),
            )

            CircleLayer(
                id = "stations-circle",
                source = stationsSource,
                filter = !has("point_count"),
                color = const(MaterialTheme.colorScheme.tertiary),
                radius = const(14.dp),
                strokeColor = const(MaterialTheme.colorScheme.onTertiary),
                strokeWidth = const(2.dp),
            )

            SymbolLayer(
                id = "stations-symbol",
                source = stationsSource,
                filter = !has("point_count"),
                textField = format(span(get("label").asString())),
                textFont = const(listOf("Noto Sans Regular")),
                textColor = const(MaterialTheme.colorScheme.onTertiary),
                textSize = const(12.sp),
                textAllowOverlap = const(true),
                textIgnorePlacement = const(true),
            )
        }

        if (!viewModel.isMapLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingIndicator(modifier = Modifier.size(64.dp))
                }
            }
        }
    }
}