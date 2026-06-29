package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcChevronLeft: ImageVector
    get() {
        if (_ChevronLeft != null) {
            return _ChevronLeft!!
        }
        _ChevronLeft = ImageVector.Builder(
            name = "SizeLarge",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color(0xFF6C757F))) {
                moveTo(15.796f, 18.704f)
                curveTo(16.007f, 18.915f, 16.126f, 19.202f, 16.126f, 19.501f)
                curveTo(16.126f, 19.8f, 16.007f, 20.086f, 15.796f, 20.298f)
                curveTo(15.585f, 20.509f, 15.298f, 20.628f, 14.999f, 20.628f)
                curveTo(14.7f, 20.628f, 14.413f, 20.509f, 14.202f, 20.298f)
                lineTo(6.702f, 12.798f)
                curveTo(6.597f, 12.693f, 6.514f, 12.569f, 6.457f, 12.432f)
                curveTo(6.401f, 12.296f, 6.371f, 12.149f, 6.371f, 12.001f)
                curveTo(6.371f, 11.853f, 6.401f, 11.706f, 6.457f, 11.569f)
                curveTo(6.514f, 11.433f, 6.597f, 11.309f, 6.702f, 11.204f)
                lineTo(14.202f, 3.704f)
                curveTo(14.413f, 3.493f, 14.7f, 3.374f, 14.999f, 3.374f)
                curveTo(15.298f, 3.374f, 15.585f, 3.493f, 15.796f, 3.704f)
                curveTo(16.007f, 3.915f, 16.126f, 4.202f, 16.126f, 4.501f)
                curveTo(16.126f, 4.8f, 16.007f, 5.086f, 15.796f, 5.298f)
                lineTo(9.094f, 12f)
                lineTo(15.796f, 18.704f)
                close()
            }
        }.build()

        return _ChevronLeft!!
    }

@Suppress("ObjectPropertyName")
private var _ChevronLeft: ImageVector? = null
