package fish.alice.openvelov.ui.maps

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fish.alice.openvelov.data.remote.BikesApi
import fish.alice.openvelov.data.remote.StationDto
import fish.alice.openvelov.data.remote.StationsApi
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import javax.inject.Inject

private const val DEFAULT_LATITUDE = 45.750000
private const val DEFAULT_LONGITUDE = 4.850000
private const val DEFAULT_ZOOM = 12.0

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stationsApi: StationsApi,
) : ViewModel() {
    var isMapLoaded by mutableStateOf(false)

    var stations by mutableStateOf<List<StationDto>>(emptyList())
        private set

    var lastCamera by mutableStateOf(CameraPosition(
        target = Position(DEFAULT_LONGITUDE, DEFAULT_LATITUDE),
        zoom = DEFAULT_ZOOM
    ))

    fun loadStations() {
        viewModelScope.launch {
            try {
                stations = stationsApi.stations()
            } catch (e: Exception) {
                println("Failed to load stations: ${e.message}")
            }
        }
    }

}

private const val OPENFREEMAP_LIBERTY = "https://tiles.openfreemap.org/styles/liberty"
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MapLibreView(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    LaunchedEffect(viewModel.isMapLoaded) {
        val activity = context as? ComponentActivity ?: return@LaunchedEffect
        val style : SystemBarStyle
 
        if (viewModel.isMapLoaded) {
            style = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        } else {
            style = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        }

        activity.enableEdgeToEdge(statusBarStyle = style)
    }

    val camera = rememberCameraState(firstPosition = viewModel.lastCamera)
    LaunchedEffect(camera.position) { viewModel.lastCamera = camera.position }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MaplibreMap(
            baseStyle = BaseStyle.Uri(OPENFREEMAP_LIBERTY),
            modifier = modifier,
            cameraState = camera,
            onMapLoadFinished = {
                viewModel.isMapLoaded = true
                viewModel.loadStations()
            },
            overlay = MapOverlay {
                viewModel.stations.forEach { station ->
                    val pos = Position(
                        station.location.longitude,
                        station.location.latitude
                    )
                    Box(
                        modifier = Modifier
                            .placedAt(pos, Alignment.Center)
                            .size(16.dp)
                            .clip(MaterialShapes.Burst.toShape())
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        )

        if (!viewModel.isMapLoaded) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoadingIndicator(
                    modifier = Modifier
                        .size(64.dp)
                )
            }
        }
    }
}

