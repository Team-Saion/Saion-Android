package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcDot: ImageVector
    get() {
        if (_DotOutline != null) {
            return _DotOutline!!
        }
        _DotOutline = ImageVector.Builder(
            name = "DotOutline",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFFB1B8B8))) {
                moveTo(14.625f, 12f)
                curveTo(14.625f, 12.519f, 14.471f, 13.027f, 14.183f, 13.458f)
                curveTo(13.894f, 13.89f, 13.484f, 14.226f, 13.005f, 14.425f)
                curveTo(12.525f, 14.624f, 11.997f, 14.676f, 11.488f, 14.575f)
                curveTo(10.979f, 14.473f, 10.511f, 14.223f, 10.144f, 13.856f)
                curveTo(9.777f, 13.489f, 9.527f, 13.021f, 9.425f, 12.512f)
                curveTo(9.324f, 12.003f, 9.376f, 11.475f, 9.575f, 10.995f)
                curveTo(9.774f, 10.516f, 10.11f, 10.106f, 10.542f, 9.817f)
                curveTo(10.973f, 9.529f, 11.481f, 9.375f, 12f, 9.375f)
                curveTo(12.696f, 9.375f, 13.364f, 9.652f, 13.856f, 10.144f)
                curveTo(14.348f, 10.636f, 14.625f, 11.304f, 14.625f, 12f)
                close()
            }
        }.build()

        return _DotOutline!!
    }

@Suppress("ObjectPropertyName")
private var _DotOutline: ImageVector? = null
