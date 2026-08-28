package com.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MechanicalBikeIcon: ImageVector
    get() {
        if (_MechanicalBike != null) return _MechanicalBike!!

        _MechanicalBike = ImageVector.Builder(
            name = "electric_bike",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE3E3E3))
            ) {
                moveTo(14.5996f, 4f)
                curveTo(15.0329f, 4f, 15.421f, 4.1163f, 15.7627f, 4.34961f)
                curveTo(16.1043f, 4.58291f, 16.35f, 4.89989f, 16.5f, 5.2998f)
                lineTo(18.2002f, 9.9502f)
                horizontalLineTo(19f)
                curveTo(20.3831f, 9.9502f, 21.5622f, 10.4374f, 22.5371f, 11.4121f)
                curveTo(23.5121f, 12.3871f, 24f, 13.5669f, 24f, 14.9502f)
                curveTo(24f, 16.3501f, 23.5164f, 17.5421f, 22.5498f, 18.5254f)
                curveTo(21.5832f, 19.5085f, 20.3999f, 20f, 19f, 20f)
                curveTo(17.8001f, 20f, 16.7462f, 19.6249f, 15.8379f, 18.875f)
                curveTo(14.9296f, 18.125f, 14.3496f, 17.1667f, 14.0996f, 16f)
                horizontalLineTo(9.90039f)
                curveTo(9.66708f, 17.1499f, 9.10007f, 18.104f, 8.2002f, 18.8623f)
                curveTo(7.3002f, 19.6206f, 6.23333f, 20f, 5f, 20f)
                curveTo(3.58333f, 20f, 2.39583f, 19.5208f, 1.4375f, 18.5625f)
                curveTo(0.479167f, 17.6042f, 0f, 16.4167f, 0f, 15f)
                curveTo(0f, 13.5833f, 0.487891f, 12.3958f, 1.46289f, 11.4375f)
                curveTo(2.43785f, 10.4793f, 3.61676f, 10f, 5f, 10f)
                curveTo(6.28333f, 10f, 7.3623f, 10.3837f, 8.2373f, 11.1504f)
                curveTo(9.11226f, 11.917f, 9.66706f, 12.8667f, 9.90039f, 14f)
                horizontalLineTo(10.5498f)
                lineTo(8.75f, 9f)
                horizontalLineTo(7f)
                verticalLineTo(7f)
                horizontalLineTo(12f)
                verticalLineTo(9f)
                horizontalLineTo(10.9004f)
                lineTo(11.25f, 10f)
                horizontalLineTo(16.0498f)
                lineTo(14.5996f, 6f)
                horizontalLineTo(12f)
                verticalLineTo(4f)
                horizontalLineTo(14.5996f)
                close()
                moveTo(5f, 12f)
                curveTo(4.15f, 12f, 3.4373f, 12.2873f, 2.8623f, 12.8623f)
                curveTo(2.2873f, 13.4373f, 2f, 14.15f, 2f, 15f)
                curveTo(2f, 15.8333f, 2.28737f, 16.5417f, 2.8623f, 17.125f)
                curveTo(3.4373f, 17.7083f, 4.15f, 18f, 5f, 18f)
                curveTo(5.68333f, 18f, 6.27103f, 17.8125f, 6.7627f, 17.4375f)
                curveTo(7.25428f, 17.0625f, 7.59982f, 16.5833f, 7.7998f, 16f)
                horizontalLineTo(5f)
                verticalLineTo(14f)
                horizontalLineTo(7.7998f)
                curveTo(7.59981f, 13.4f, 7.25433f, 12.9165f, 6.7627f, 12.5498f)
                curveTo(6.27103f, 12.1831f, 5.68333f, 12f, 5f, 12f)
                close()
                moveTo(19.9004f, 14.6504f)
                lineTo(18f, 15.3496f)
                lineTo(17.0498f, 12.7002f)
                curveTo(16.7166f, 12.9835f, 16.4587f, 13.3169f, 16.2754f, 13.7002f)
                curveTo(16.0921f, 14.0835f, 16f, 14.5167f, 16f, 15f)
                curveTo(16f, 15.8333f, 16.2874f, 16.5417f, 16.8623f, 17.125f)
                curveTo(17.4373f, 17.7083f, 18.15f, 18f, 19f, 18f)
                curveTo(19.85f, 18f, 20.5627f, 17.7083f, 21.1377f, 17.125f)
                curveTo(21.7126f, 16.5417f, 22f, 15.8333f, 22f, 15f)
                curveTo(22f, 14.15f, 21.7127f, 13.4373f, 21.1377f, 12.8623f)
                curveTo(20.5627f, 12.2873f, 19.85f, 12f, 19f, 12f)
                horizontalLineTo(18.9004f)
                lineTo(19.9004f, 14.6504f)
                close()
                moveTo(12.7002f, 14f)
                horizontalLineTo(14.0996f)
                curveTo(14.1829f, 13.6167f, 14.2958f, 13.2581f, 14.4375f, 12.9248f)
                curveTo(14.5792f, 12.5915f, 14.7667f, 12.2833f, 15f, 12f)
                horizontalLineTo(11.9502f)
                lineTo(12.7002f, 14f)
                close()
            }
        }.build()
        
        return _MechanicalBike!!
    }

private var _MechanicalBike: ImageVector? = null

