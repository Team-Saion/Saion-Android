package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcWarning: ImageVector
    get() {
        if (_IcWarning != null) {
            return _IcWarning!!
        }
        _IcWarning = ImageVector.Builder(
            name = "Warning",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Transparent)) {
                moveTo(21.75f, 12f)
                curveTo(21.75f, 13.928f, 21.178f, 15.813f, 20.107f, 17.417f)
                curveTo(19.035f, 19.02f, 17.513f, 20.27f, 15.731f, 21.008f)
                curveTo(13.95f, 21.746f, 11.989f, 21.939f, 10.098f, 21.563f)
                curveTo(8.207f, 21.187f, 6.469f, 20.258f, 5.106f, 18.894f)
                curveTo(3.742f, 17.531f, 2.814f, 15.793f, 2.437f, 13.902f)
                curveTo(2.061f, 12.011f, 2.254f, 10.05f, 2.992f, 8.269f)
                curveTo(3.73f, 6.487f, 4.98f, 4.965f, 6.583f, 3.893f)
                curveTo(8.187f, 2.822f, 10.072f, 2.25f, 12f, 2.25f)
                curveTo(14.585f, 2.253f, 17.063f, 3.281f, 18.891f, 5.109f)
                curveTo(20.719f, 6.937f, 21.747f, 9.415f, 21.75f, 12f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6C757F))) {
                moveTo(12f, 2.25f)
                curveTo(10.072f, 2.25f, 8.187f, 2.822f, 6.583f, 3.893f)
                curveTo(4.98f, 4.965f, 3.73f, 6.487f, 2.992f, 8.269f)
                curveTo(2.254f, 10.05f, 2.061f, 12.011f, 2.437f, 13.902f)
                curveTo(2.814f, 15.793f, 3.742f, 17.531f, 5.106f, 18.894f)
                curveTo(6.469f, 20.258f, 8.207f, 21.187f, 10.098f, 21.563f)
                curveTo(11.989f, 21.939f, 13.95f, 21.746f, 15.731f, 21.008f)
                curveTo(17.513f, 20.27f, 19.035f, 19.02f, 20.107f, 17.417f)
                curveTo(21.178f, 15.813f, 21.75f, 13.928f, 21.75f, 12f)
                curveTo(21.747f, 9.415f, 20.719f, 6.937f, 18.891f, 5.109f)
                curveTo(17.063f, 3.281f, 14.585f, 2.253f, 12f, 2.25f)
                close()
                moveTo(11.25f, 7.5f)
                curveTo(11.25f, 7.301f, 11.329f, 7.11f, 11.47f, 6.97f)
                curveTo(11.61f, 6.829f, 11.801f, 6.75f, 12f, 6.75f)
                curveTo(12.199f, 6.75f, 12.39f, 6.829f, 12.53f, 6.97f)
                curveTo(12.671f, 7.11f, 12.75f, 7.301f, 12.75f, 7.5f)
                verticalLineTo(12.75f)
                curveTo(12.75f, 12.949f, 12.671f, 13.14f, 12.53f, 13.28f)
                curveTo(12.39f, 13.421f, 12.199f, 13.5f, 12f, 13.5f)
                curveTo(11.801f, 13.5f, 11.61f, 13.421f, 11.47f, 13.28f)
                curveTo(11.329f, 13.14f, 11.25f, 12.949f, 11.25f, 12.75f)
                verticalLineTo(7.5f)
                close()
                moveTo(12f, 17.25f)
                curveTo(11.778f, 17.25f, 11.56f, 17.184f, 11.375f, 17.06f)
                curveTo(11.19f, 16.937f, 11.046f, 16.761f, 10.961f, 16.556f)
                curveTo(10.875f, 16.35f, 10.853f, 16.124f, 10.897f, 15.906f)
                curveTo(10.94f, 15.687f, 11.047f, 15.487f, 11.205f, 15.33f)
                curveTo(11.362f, 15.172f, 11.562f, 15.065f, 11.781f, 15.022f)
                curveTo(11.999f, 14.978f, 12.225f, 15f, 12.431f, 15.086f)
                curveTo(12.636f, 15.171f, 12.812f, 15.315f, 12.935f, 15.5f)
                curveTo(13.059f, 15.685f, 13.125f, 15.903f, 13.125f, 16.125f)
                curveTo(13.125f, 16.423f, 13.007f, 16.709f, 12.795f, 16.92f)
                curveTo(12.585f, 17.132f, 12.298f, 17.25f, 12f, 17.25f)
                close()
            }
        }.build()

        return _IcWarning!!
    }

@Suppress("ObjectPropertyName")
private var _IcWarning: ImageVector? = null
