package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcPlus: ImageVector
    get() {
        if (_Plus != null) {
            return _Plus!!
        }
        _Plus = ImageVector.Builder(
            name = "Plus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFFD2D6CF))) {
                moveTo(21.375f, 12f)
                curveTo(21.375f, 12.298f, 21.257f, 12.585f, 21.045f, 12.795f)
                curveTo(20.834f, 13.007f, 20.548f, 13.125f, 20.25f, 13.125f)
                horizontalLineTo(13.125f)
                verticalLineTo(20.25f)
                curveTo(13.125f, 20.548f, 13.007f, 20.834f, 12.795f, 21.045f)
                curveTo(12.585f, 21.257f, 12.298f, 21.375f, 12f, 21.375f)
                curveTo(11.702f, 21.375f, 11.415f, 21.257f, 11.205f, 21.045f)
                curveTo(10.993f, 20.834f, 10.875f, 20.548f, 10.875f, 20.25f)
                verticalLineTo(13.125f)
                horizontalLineTo(3.75f)
                curveTo(3.452f, 13.125f, 3.165f, 13.007f, 2.954f, 12.795f)
                curveTo(2.744f, 12.585f, 2.625f, 12.298f, 2.625f, 12f)
                curveTo(2.625f, 11.702f, 2.744f, 11.415f, 2.954f, 11.205f)
                curveTo(3.165f, 10.993f, 3.452f, 10.875f, 3.75f, 10.875f)
                horizontalLineTo(10.875f)
                verticalLineTo(3.75f)
                curveTo(10.875f, 3.452f, 10.993f, 3.165f, 11.205f, 2.954f)
                curveTo(11.415f, 2.744f, 11.702f, 2.625f, 12f, 2.625f)
                curveTo(12.298f, 2.625f, 12.585f, 2.744f, 12.795f, 2.954f)
                curveTo(13.007f, 3.165f, 13.125f, 3.452f, 13.125f, 3.75f)
                verticalLineTo(10.875f)
                horizontalLineTo(20.25f)
                curveTo(20.548f, 10.875f, 20.834f, 10.993f, 21.045f, 11.205f)
                curveTo(21.257f, 11.415f, 21.375f, 11.702f, 21.375f, 12f)
                close()
            }
        }.build()

        return _Plus!!
    }

@Suppress("ObjectPropertyName")
private var _Plus: ImageVector? = null
