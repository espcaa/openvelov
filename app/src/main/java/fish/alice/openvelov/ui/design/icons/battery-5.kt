package com.example.test

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val BatteryFiveIcon: ImageVector
  get() {
    if (_battery_5_bar != null) {
      return _battery_5_bar!!
    }
    _battery_5_bar =
      ImageVector.Builder(
          name = "battery_5_bar",
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
            moveTo(8f, 22f)
            quadTo(7.58f, 22f, 7.29f, 21.71f)
            quadTo(7f, 21.43f, 7f, 21f)
            verticalLineTo(5f)
            quadTo(7f, 4.57f, 7.29f, 4.29f)
            reflectiveQuadTo(8f, 4f)
            horizontalLineToRelative(2f)
            verticalLineTo(3f)
            quadTo(10f, 2.57f, 10.29f, 2.29f)
            reflectiveQuadTo(11f, 2f)
            horizontalLineToRelative(2f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(14f, 3f)
            verticalLineTo(4f)
            horizontalLineToRelative(2f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(17f, 5f)
            verticalLineTo(21f)
            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
            reflectiveQuadTo(16f, 22f)
            horizontalLineTo(8f)
            close()
            moveTo(9f, 10f)
            horizontalLineToRelative(6f)
            verticalLineTo(6f)
            horizontalLineTo(9f)
            verticalLineToRelative(4f)
            close()
          }
        }
        .build()
    return _battery_5_bar!!
  }

private var _battery_5_bar: ImageVector? = null
