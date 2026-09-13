package fish.alice.openvelov.ui.maps

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.test.LocationFilledIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import fish.alice.openvelov.data.remote.StationDto
import fish.alice.openvelov.data.remote.StationsApi
import fish.alice.openvelov.ui.design.icons.LocationIcon
import fish.alice.openvelov.ui.design.map.MapPin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.Feature.get
import org.maplibre.compose.expressions.dsl.Feature.has
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.convertToNumber
import org.maplibre.compose.expressions.dsl.convertToString
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.not
import org.maplibre.compose.expressions.dsl.offset
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.expressions.dsl.step
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.location.LocationPuck
import org.maplibre.compose.location.LocationTrackingEffect
import org.maplibre.compose.location.mostAccurateBearing
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberDefaultOrientationProvider
import org.maplibre.compose.location.rememberLocationState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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
    private val _geoJsonData = MutableStateFlow<GeoJsonData>(
        GeoJsonData.JsonString("""{"type": "FeatureCollection", "features": []}""")
    )
    val geoJsonData: StateFlow<GeoJsonData> = _geoJsonData.asStateFlow()

    private val _cameraPosition = MutableStateFlow(
        CameraPosition(
            target = Position(DEFAULT_LONGITUDE, DEFAULT_LATITUDE),
            zoom = DEFAULT_ZOOM
        )
    )

    fun onCameraMoved(newPosition: CameraPosition) {
        _cameraPosition.value = newPosition
    }

    fun loadStations() {
        if (isMapLoaded) return
        viewModelScope.launch {
            try {
                Log.d("MapViewModel", "Loading stations from API...")
                val fetchedStations = stationsApi.stations()
                _rawStations.value = fetchedStations
                val parsedGeoJson = withContext(Dispatchers.Default) {
                    fetchedStations.toGeoJsonData()
                }
                _geoJsonData.value = parsedGeoJson
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, FlowPreview::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun VelovMapView(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val stations by viewModel.rawStations.collectAsStateWithLifecycle()
    val geoJsonData by viewModel.geoJsonData.collectAsStateWithLifecycle()

    val stationIconCenter = MaterialTheme.colorScheme.primaryFixed
    val stationIconOutside = MaterialTheme.colorScheme.onPrimaryFixed

    val stationPainter = rememberVectorPainter(
        image = MapPin(
            centerColor = stationIconCenter,
            pinColor = stationIconOutside
        )
    )

    val camera = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(DEFAULT_LONGITUDE, DEFAULT_LATITUDE),
            zoom = DEFAULT_ZOOM
        )
    )

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )
    var selectedStation by remember { mutableStateOf<StationDto?>(null) }

    val locationProvider = rememberDefaultLocationProvider()
    val orientationProvider =
        rememberDefaultOrientationProvider()
    val locationState =
        rememberLocationState(
            provider = locationProvider,
            orientationProvider = orientationProvider,
        )

    var isTrackingLocation by remember { mutableStateOf(false) }

    LaunchedEffect(camera) {
        snapshotFlow { camera.isCameraMoving }
            .collect { isMoving ->
                if (isMoving && camera.moveReason == CameraMoveReason.GESTURE) {
                    isTrackingLocation = false
                }
            }
    }

    LaunchedEffect(camera) {
        snapshotFlow { camera.position }
            .distinctUntilChanged()
            .debounce(300.milliseconds)
            .collect { viewModel.onCameraMoved(it) }
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
                data = geoJsonData,
                options = GeoJsonOptions(
                    cluster = true,
                    clusterRadius = 50,
                    clusterMaxZoom = 14,
                    synchronousUpdate = false
                )
            )

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
                color = const(MaterialTheme.colorScheme.secondaryFixed),
                strokeColor = const(MaterialTheme.colorScheme.onSecondaryFixed),
                strokeWidth = const(0.dp),
                onClick = { features ->
                    val clusterId = features.firstOrNull()
                        ?.properties?.get("cluster_id")?.jsonPrimitive?.int
                    if (clusterId != null) {
                        val target = (features.first().geometry as? Point)?.coordinates
                            ?: return@CircleLayer ClickResult.Pass
                        scope.launch {
                            camera.animateTo(
                                CameraPosition(
                                    target = target,
                                    zoom = camera.position.zoom + 2.0,
                                )
                            )
                        }
                        ClickResult.Consume
                    } else {
                        ClickResult.Pass
                    }
                }
            )

            SymbolLayer(
                id = "cluster-count",
                source = stationsSource,
                filter = has("point_count"),
                textField = format(span(get("point_count_abbreviated").convertToString())),
                textFont = const(listOf("Noto Sans Bold")),
                textColor = const(MaterialTheme.colorScheme.onSecondaryFixed),
                textSize = const(13.sp),
                textAllowOverlap = const(true),
                textIgnorePlacement = const(true),
            )

            SymbolLayer(
                id = "stations-icon",
                source = stationsSource,
                filter = !has("point_count"),
                iconImage = image(
                    value = stationPainter,
                    size = DpSize(48.dp, 48.dp)
                ),
                iconAllowOverlap = const(true),
                iconIgnorePlacement = const(false),
                onClick = { features ->
                    val id = features.firstOrNull()
                        ?.properties?.get("id")?.jsonPrimitive?.content
                    val station = stations.firstOrNull { it.id == id }
                    if (station != null) {
                        selectedStation = station
                        scope.launch {
                            (features.first().geometry as? Point)?.coordinates?.let {
                                camera.animateTo(
                                    CameraPosition(
                                        target = it,
                                        zoom = 16.0,
                                    )
                                )
                            }
                        }
                        ClickResult.Consume
                    } else {
                        ClickResult.Pass
                    }
                }
            )

            SymbolLayer(
                id = "stations-symbol",
                source = stationsSource,
                filter = !has("point_count"),
                textField = format(span(get("label").asString())),
                textFont = const(listOf("Noto Sans Bold")),
                textColor = const(MaterialTheme.colorScheme.onPrimaryFixed),
                textSize = const(16.sp),
                textOffset = offset(0f.em, (-0.3f).em),
                textAllowOverlap = const(true),
                textIgnorePlacement = const(true),
            )

            LocationPuck(
                idPrefix = "user",
                location = locationState.location,
                bearing = locationState.mostAccurateBearing(),
                cameraState = camera,
            )

            LocationTrackingEffect(
                locationState = locationState,
                enabled = isTrackingLocation
            ) {
                camera.animateTo(
                    CameraPosition(
                        target = currentLocation.position.value,
                        zoom = 15.0
                    )
                )
            }

        }

        FloatingActionButton(
            onClick = {
                isTrackingLocation = !isTrackingLocation
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = MaterialTheme.shapes.largeIncreased,
            containerColor = if (isTrackingLocation) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            contentColor = if (isTrackingLocation) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                imageVector = if (isTrackingLocation) LocationFilledIcon else LocationIcon,
                contentDescription = "",
                tint = if (isTrackingLocation) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        selectedStation?.let { station ->
            ModalBottomSheet(
                onDismissRequest = { selectedStation = null },
                sheetState = sheetState,
            ) {
                StationDetailsSheet(
                    station = station,
                )
            }
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