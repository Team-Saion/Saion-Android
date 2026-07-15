package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcPerson: ImageVector
    get() {
        if (_Person != null) {
            return _Person!!
        }
        _Person = ImageVector.Builder(
            name = "StyleFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color(0xFF6C757F))) {
                moveTo(21.65f, 20.625f)
                curveTo(21.584f, 20.739f, 21.489f, 20.834f, 21.375f, 20.899f)
                curveTo(21.261f, 20.965f, 21.132f, 21f, 21f, 21f)
                horizontalLineTo(3f)
                curveTo(2.868f, 21f, 2.739f, 20.965f, 2.625f, 20.899f)
                curveTo(2.511f, 20.833f, 2.417f, 20.739f, 2.351f, 20.625f)
                curveTo(2.285f, 20.511f, 2.251f, 20.381f, 2.251f, 20.25f)
                curveTo(2.251f, 20.118f, 2.285f, 19.989f, 2.351f, 19.875f)
                curveTo(3.779f, 17.406f, 5.979f, 15.637f, 8.547f, 14.797f)
                curveTo(7.277f, 14.041f, 6.29f, 12.889f, 5.738f, 11.518f)
                curveTo(5.186f, 10.147f, 5.1f, 8.632f, 5.492f, 7.207f)
                curveTo(5.884f, 5.782f, 6.733f, 4.525f, 7.909f, 3.629f)
                curveTo(9.084f, 2.733f, 10.522f, 2.247f, 12f, 2.247f)
                curveTo(13.478f, 2.247f, 14.915f, 2.733f, 16.091f, 3.629f)
                curveTo(17.267f, 4.525f, 18.116f, 5.782f, 18.508f, 7.207f)
                curveTo(18.9f, 8.632f, 18.814f, 10.147f, 18.262f, 11.518f)
                curveTo(17.71f, 12.889f, 16.723f, 14.041f, 15.453f, 14.797f)
                curveTo(18.021f, 15.637f, 20.221f, 17.406f, 21.649f, 19.875f)
                curveTo(21.715f, 19.989f, 21.75f, 20.118f, 21.75f, 20.25f)
                curveTo(21.75f, 20.382f, 21.715f, 20.511f, 21.65f, 20.625f)
                close()
            }
        }.build()

        return _Person!!
    }

@Suppress("ObjectPropertyName")
private var _Person: ImageVector? = null
