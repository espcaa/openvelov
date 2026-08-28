package fish.alice.openvelov.ui.maps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.ElectricBikeIcon
import com.composables.MechanicalBikeIcon
import fish.alice.openvelov.data.remote.StationDto
import fish.alice.openvelov.ui.design.icons.BikeDockIcon
import fish.alice.openvelov.ui.theme.emphasizedTypography
import fish.alice.openvelov.utils.extractStationName

@OptIn( ExperimentalMaterial3ExpressiveApi::class)
@Composable fun StationDetailsSheet(
    modifier: Modifier = Modifier,
    station: StationDto,
) {
    val loading by remember { mutableStateOf(true) }

    Column(
        modifier= modifier
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
                .fillMaxWidth()
                .padding(top = 8.dp),
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
                count = station.availabilities.main.bikes.electrical
            )
        }
        // surface for individual bikes
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (loading) {
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
                    Text(text = "Individual bikes will be displayed here.", fontSize = 15.sp)
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
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryFixedDim,
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
                color = MaterialTheme.colorScheme.onPrimaryFixed
            )
        }
    }
}