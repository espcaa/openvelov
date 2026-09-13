package fish.alice.openvelov.ui.maps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.ElectricBikeIcon
import com.composables.MechanicalBikeIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import fish.alice.openvelov.data.remote.BikeDto
import fish.alice.openvelov.data.remote.BikesApi
import fish.alice.openvelov.data.remote.StationDto
import fish.alice.openvelov.ui.design.icons.BikeDockIcon
import fish.alice.openvelov.ui.theme.emphasizedTypography
import fish.alice.openvelov.utils.extractStationName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.test.BatteryFiveIcon
import com.example.test.BatteryFourIcon
import com.example.test.BatteryFullIcon
import com.example.test.BatteryOneIcon
import com.example.test.BatterySixIcon
import com.example.test.BatteryThreeIcon
import com.example.test.BatteryTwoIcon
import com.example.test.BatteryZeroIcon
import com.example.test.StarFilledIcon
import com.example.test.StarIcon
import fish.alice.openvelov.R
import fish.alice.openvelov.data.remote.BatteryDto
import fish.alice.openvelov.ui.theme.extendedColorScheme
import org.maplibre.compose.expressions.dsl.switch

@HiltViewModel
class StationDetailsViewModel @Inject constructor(
    private val bikesApi: BikesApi,
) : ViewModel() {
    var areBikesLoaded by mutableStateOf(false)
    private val _bikes = MutableStateFlow<List<BikeDto>>(emptyList())
    var bikes : StateFlow<List<BikeDto>> = _bikes.asStateFlow()

    fun loadBikes(stationId: Int) {
        viewModelScope.launch {
            try {
                _bikes.value = bikesApi.bikes(stationId)
            } catch (e: Exception) {
                Log.e(
                    "StationDetailsViewModel",
                    "Error loading bikes for station $stationId: ${e.message}",
                    e
                )
            } finally {
                areBikesLoaded = true
            }
        }
    }
}


@OptIn( ExperimentalMaterial3ExpressiveApi::class)
@Composable fun StationDetailsSheet(
    modifier: Modifier = Modifier,
    station: StationDto,
    viewModel: StationDetailsViewModel = hiltViewModel(),
    ) {

    LaunchedEffect(station.number) {
        viewModel.areBikesLoaded = false
        viewModel.loadBikes(station.number)
    }

    Column(
        modifier= modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // emphasized station name & id in a pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = extractStationName(station.name).name,
                    style = MaterialTheme.typography.displayLargeEmphasized
                )
//            Surface(
//                shape = MaterialTheme.shapes.large,
//                color = MaterialTheme.colorScheme.primary,
//            ) {
//                Text(
//                    text = station.number.toString(),
//                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                    fontSize = 15.sp,
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//            }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvailabilityPill(
                    icon = { Icon(imageVector = BikeDockIcon, contentDescription = null) },
                    count = station.availabilities.main.stands
                )
                AvailabilityPill(
                    icon = { Icon(imageVector = MechanicalBikeIcon, contentDescription = null) },
                    count = station.availabilities.main.bikes.mechanical
                )
                AvailabilityPill(
                    icon = { Icon(imageVector = ElectricBikeIcon, contentDescription = null) },
                    count = station.availabilities.main.bikes.electrical,
                    contentColor = MaterialTheme.extendedColorScheme.onElectricBikeContainer,
                    containerColor = MaterialTheme.extendedColorScheme.electricBikeContainer
                )
            }
        }
        // surface for individual bikes
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!viewModel.areBikesLoaded) {
                    Column(
                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ContainedLoadingIndicator(
                            modifier = Modifier.size(96.dp)
                        )
                    }
                } else {
                    if (viewModel.bikes.collectAsState().value.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No bikes available",
                                style = MaterialTheme.typography.bodyLargeEmphasized,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // sort by stand number
                        val sortedBikes = viewModel.bikes.collectAsState().value.sortedBy { it.standNumber }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(
                                items = sortedBikes,
                                key = { _, bike -> bike.standNumber }
                            ) { index, bike ->
                                BikeCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    bike = bike,
                                    firstItem = index == 0,
                                    lastItem = index == sortedBikes.lastIndex
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvailabilityPill(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    count: Int,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
        modifier = modifier.defaultMinSize(minWidth = 64.dp, minHeight = 32.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon()
            Text(
                text = "$count",
                style = MaterialTheme.typography.bodyLargeEmphasized,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable fun BikeCard(
    modifier: Modifier = Modifier,
    bike: BikeDto,
    firstItem: Boolean = false,
    lastItem: Boolean = false,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = RoundedCornerShape(
            topStart = if (firstItem) 32.dp else 0.dp,
            topEnd = if (firstItem) 32.dp else 0.dp,
            bottomStart = if (lastItem) 32.dp else 0.dp,
            bottomEnd = if (lastItem) 32.dp else 0.dp,
        ),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                color = if (bike.type == "MECHANICAL") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.extendedColorScheme.electricBikeContainer,
                shape = MaterialShapes.Circle.toShape(),
                modifier = Modifier.size(48.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = bike.standNumber.toString(),
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        color = if (bike.type == "MECHANICAL") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.extendedColorScheme.onElectricBikeContainer,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 20.sp
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // bike icon
                if (bike.type == "MECHANICAL") {
                    Icon(
                        imageVector = MechanicalBikeIcon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (bike.type == "ELECTRICAL") {
                    Icon(
                        imageVector = ElectricBikeIcon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = bike.number.toString(),
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    fontSize = 16.sp
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (bike.battery != null) {
                        BatteryDetails(battery = bike.battery)
                    }
                    if (bike.rating != null) {
                        RatingStars(rating = bike.rating.value)
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable fun RatingStars(
    modifier: Modifier = Modifier,
    rating: Double,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val score: Int = if (rating >= 95) {
            3
        } else if (rating >= 90) {
            2
        } else if (rating >= 85) {
            1
        } else {
            0
        }

        for (i in 1..3) {
            if (i <= score) {
                Icon(
                    imageVector = StarFilledIcon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = StarIcon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable fun BatteryDetails(
    modifier: Modifier = Modifier,
    battery: BatteryDto
) {
    val batteryIcon = when (battery.percentage) {
        in 0..5 -> BatteryZeroIcon
        in 6..19 -> BatteryOneIcon
        in 20..34 -> BatteryTwoIcon
        in 35..49 -> BatteryThreeIcon
        in 50..64 -> BatteryFourIcon
        in 65..79 -> BatteryFiveIcon
        in 80..94 -> BatterySixIcon
        else -> BatteryFullIcon
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = batteryIcon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "${battery.percentage}%",
            style = MaterialTheme.typography.bodyLargeEmphasized,
            fontSize = 16.sp
        )
    }
}