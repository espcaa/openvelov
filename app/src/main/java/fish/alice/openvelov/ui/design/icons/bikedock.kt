package fish.alice.openvelov.ui.design.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val BikeDockIcon: ImageVector
  get() {
    if (_bike_dock != null) {
      return _bike_dock!!
    }
    _bike_dock =
      ImageVector.Builder(
          name = "bike_dock",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(3.95f, 21f)
            quadTo(3.53f, 21f, 3.26f, 20.68f)
            reflectiveQuadTo(3f, 19.93f)
            reflectiveQuadTo(3.25f, 19.19f)
            reflectiveQuadTo(3.9f, 18.75f)
            lineTo(7.25f, 17.8f)
            lineTo(8.78f, 4.77f)
            quadTo(8.88f, 4.02f, 9.44f, 3.51f)
            reflectiveQuadTo(10.78f, 3f)
            horizontalLineTo(13.2f)
            quadToRelative(0.78f, 0f, 1.34f, 0.51f)
            reflectiveQuadTo(15.2f, 4.77f)
            lineTo(16.73f, 17.8f)
            lineToRelative(3.32f, 0.95f)
            quadToRelative(0.43f, 0.13f, 0.69f, 0.43f)
            reflectiveQuadTo(21f, 19.9f)
            quadToRelative(0f, 0.45f, -0.29f, 0.78f)
            reflectiveQuadTo(20f, 21f)
            horizontalLineTo(3.95f)
            close()
            moveTo(11f, 18f)
            horizontalLineToRelative(2f)
            verticalLineTo(6f)
            quadTo(13f, 5.57f, 12.71f, 5.29f)
            reflectiveQuadTo(12f, 5f)
            reflectiveQuadTo(11.29f, 5.29f)
            reflectiveQuadTo(11f, 6f)
            verticalLineTo(18f)
            close()
          }
        }
        .build()
    return _bike_dock!!
  }

private var _bike_dock: ImageVector? = null
