package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcBell: ImageVector
    get() {
        if (_Bell != null) {
            return _Bell!!
        }
        _Bell = ImageVector.Builder(
            name = "StyleFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF6C757F))) {
                moveTo(15.532f, 19.25f)
                curveTo(15.304f, 19.894f, 14.906f, 20.469f, 14.372f, 20.905f)
                curveTo(13.702f, 21.451f, 12.865f, 21.75f, 12f, 21.75f)
                curveTo(11.136f, 21.75f, 10.299f, 21.451f, 9.629f, 20.905f)
                curveTo(9.095f, 20.469f, 8.697f, 19.894f, 8.469f, 19.25f)
                horizontalLineTo(15.532f)
                close()
                moveTo(12f, 2.25f)
                curveTo(13.99f, 2.25f, 15.898f, 2.993f, 17.304f, 4.314f)
                curveTo(18.711f, 5.635f, 19.5f, 7.428f, 19.5f, 9.296f)
                curveTo(19.501f, 12.408f, 20.274f, 14.789f, 20.794f, 15.631f)
                curveTo(20.927f, 15.845f, 20.998f, 16.089f, 20.999f, 16.336f)
                curveTo(21f, 16.584f, 20.931f, 16.828f, 20.799f, 17.042f)
                curveTo(20.668f, 17.257f, 20.479f, 17.436f, 20.251f, 17.56f)
                curveTo(20.023f, 17.684f, 19.764f, 17.75f, 19.5f, 17.75f)
                horizontalLineTo(4.501f)
                curveTo(4.237f, 17.75f, 3.978f, 17.685f, 3.75f, 17.561f)
                curveTo(3.521f, 17.437f, 3.332f, 17.257f, 3.201f, 17.042f)
                curveTo(3.069f, 16.828f, 3.001f, 16.584f, 3.001f, 16.336f)
                curveTo(3.002f, 16.089f, 3.073f, 15.845f, 3.206f, 15.631f)
                curveTo(3.726f, 14.789f, 4.5f, 12.408f, 4.501f, 9.296f)
                curveTo(4.501f, 7.428f, 5.29f, 5.635f, 6.697f, 4.314f)
                curveTo(8.103f, 2.993f, 10.011f, 2.25f, 12f, 2.25f)
                close()
            }
        }.build()

        return _Bell!!
    }

@Suppress("ObjectPropertyName")
private var _Bell: ImageVector? = null
