package fish.alice.openvelov.ui.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.maplibre.spatialk.geojson.Position
import org.maplibre.compose.overlay.MapOverlayScope


@Composable
fun MapOverlayScope.StationBadge(position: Position, electricalBikes: Int, mechanicalBikes: Int, stands: Int) {
    val maxBikes = electricalBikes + mechanicalBikes + stands
    val currentBikes = electricalBikes + mechanicalBikes

    val bgColor = when {
        currentBikes < maxBikes * 0.2 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = Modifier
            .placedAt(position, Alignment.Center)
            .size(28.dp)
            .clip(CircleShape)
            .background(bgColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$currentBikes / $maxBikes",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}à

@Composable
fun MapOverlayScope.ClusterBadge(position: Position, count: Int) {
    Box(
        modifier = Modifier
            .placedAt(position, Alignment.Center)
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$count",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}