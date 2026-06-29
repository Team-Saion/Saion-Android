package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcChevronRight: ImageVector
    get() {
        if (_ChevronRight != null) {
            return _ChevronRight!!
        }
        _ChevronRight = ImageVector.Builder(
            name = "ChevronRight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color(0xFF6C757F))) {
                moveTo(17.296f, 12.796f)
                lineTo(9.796f, 20.296f)
                curveTo(9.585f, 20.507f, 9.298f, 20.626f, 8.999f, 20.626f)
                curveTo(8.7f, 20.626f, 8.414f, 20.507f, 8.202f, 20.296f)
                curveTo(7.991f, 20.085f, 7.872f, 19.798f, 7.872f, 19.499f)
                curveTo(7.872f, 19.2f, 7.991f, 18.914f, 8.202f, 18.702f)
                lineTo(14.906f, 12f)
                lineTo(8.204f, 5.296f)
                curveTo(8.099f, 5.191f, 8.016f, 5.067f, 7.96f, 4.93f)
                curveTo(7.903f, 4.794f, 7.874f, 4.647f, 7.874f, 4.499f)
                curveTo(7.874f, 4.351f, 7.903f, 4.205f, 7.96f, 4.068f)
                curveTo(8.016f, 3.931f, 8.099f, 3.807f, 8.204f, 3.702f)
                curveTo(8.309f, 3.598f, 8.433f, 3.515f, 8.57f, 3.458f)
                curveTo(8.706f, 3.401f, 8.853f, 3.372f, 9.001f, 3.372f)
                curveTo(9.149f, 3.372f, 9.295f, 3.401f, 9.432f, 3.458f)
                curveTo(9.569f, 3.515f, 9.693f, 3.598f, 9.798f, 3.702f)
                lineTo(17.298f, 11.202f)
                curveTo(17.403f, 11.307f, 17.486f, 11.431f, 17.542f, 11.568f)
                curveTo(17.599f, 11.705f, 17.628f, 11.851f, 17.628f, 12f)
                curveTo(17.628f, 12.148f, 17.598f, 12.294f, 17.541f, 12.431f)
                curveTo(17.484f, 12.568f, 17.401f, 12.692f, 17.296f, 12.796f)
                close()
            }
        }.build()

        return _ChevronRight!!
    }

@Suppress("ObjectPropertyName")
private var _ChevronRight: ImageVector? = null
