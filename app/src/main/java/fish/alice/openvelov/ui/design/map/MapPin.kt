package fish.alice.openvelov.ui.design.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun MapPin(
    pinColor: Color = Color(0xFFFF0000),
    centerColor: Color = Color(0xFF000000)
): ImageVector {
    return ImageVector.Builder(
        name = "MapPin",
        defaultWidth = 240.dp,
        defaultHeight = 240.dp,
        viewportWidth = 240f,
        viewportHeight = 240f
    ).apply {
        // Center circle inner fill
        path(
            fill = SolidColor(centerColor)
        ) {
            moveTo(182f, 98.5f)
            arcTo(62.5f, 62.5f, 0f, false, true, 119.5f, 161f)
            arcTo(62.5f, 62.5f, 0f, false, true, 57f, 98.5f)
            arcTo(62.5f, 62.5f, 0f, false, true, 182f, 98.5f)
            close()
        }
        // Outer pin body
        path(
            fill = SolidColor(pinColor),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(120f, 20f)
            curveTo(141.167f, 20f, 159.792f, 27.2921f, 175.875f, 41.876f)
            curveTo(191.958f, 56.4598f, 200f, 76.0416f, 200f, 100.621f)
            curveTo(200f, 107.995f, 198.542f, 115.737f, 195.625f, 123.849f)
            curveTo(192.708f, 131.96f, 188.458f, 140.399f, 182.875f, 149.166f)
            curveTo(177.292f, 157.933f, 170.333f, 166.986f, 162f, 176.326f)
            curveTo(153.667f, 185.666f, 144.083f, 195.252f, 133.25f, 205.084f)
            curveTo(131.417f, 206.723f, 129.333f, 207.952f, 127f, 208.771f)
            curveTo(124.667f, 209.591f, 122.333f, 210f, 120f, 210f)
            curveTo(117.667f, 210f, 115.333f, 209.591f, 113f, 208.771f)
            curveTo(110.667f, 207.952f, 108.583f, 206.723f, 106.75f, 205.084f)
            curveTo(95.9167f, 195.252f, 86.3333f, 185.666f, 78f, 176.326f)
            curveTo(69.6668f, 166.986f, 62.7083f, 157.933f, 57.125f, 149.166f)
            curveTo(51.5417f, 140.399f, 47.2917f, 131.96f, 44.375f, 123.849f)
            curveTo(41.4584f, 115.737f, 40f, 107.995f, 40f, 100.621f)
            curveTo(40f, 76.0416f, 48.0417f, 56.4598f, 64.125f, 41.876f)
            curveTo(80.2083f, 27.2921f, 98.8333f, 20f, 120f, 20f)
            close()
            moveTo(119.5f, 36f)
            curveTo(84.9822f, 36f, 57f, 63.9822f, 57f, 98.5f)
            curveTo(57f, 133.018f, 84.9822f, 161f, 119.5f, 161f)
            curveTo(154.018f, 161f, 182f, 133.018f, 182f, 98.5f)
            curveTo(182f, 63.9822f, 154.018f, 36f, 119.5f, 36f)
            close()
        }
    }.build()
}