package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcHome: ImageVector
    get() {
        if (_Home != null) {
            return _Home!!
        }
        _Home = ImageVector.Builder(
            name = "StyleFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color(0xFF6C757F))) {
                moveTo(21f, 11.25f)
                verticalLineTo(20.25f)
                curveTo(21f, 20.449f, 20.921f, 20.64f, 20.78f, 20.78f)
                curveTo(20.64f, 20.921f, 20.449f, 21f, 20.25f, 21f)
                horizontalLineTo(15f)
                curveTo(14.801f, 21f, 14.61f, 20.921f, 14.47f, 20.78f)
                curveTo(14.329f, 20.64f, 14.25f, 20.449f, 14.25f, 20.25f)
                verticalLineTo(15.375f)
                curveTo(14.25f, 15.276f, 14.21f, 15.18f, 14.14f, 15.11f)
                curveTo(14.07f, 15.04f, 13.974f, 15f, 13.875f, 15f)
                horizontalLineTo(10.125f)
                curveTo(10.026f, 15f, 9.93f, 15.04f, 9.86f, 15.11f)
                curveTo(9.79f, 15.18f, 9.75f, 15.276f, 9.75f, 15.375f)
                verticalLineTo(20.25f)
                curveTo(9.75f, 20.449f, 9.671f, 20.64f, 9.53f, 20.78f)
                curveTo(9.39f, 20.921f, 9.199f, 21f, 9f, 21f)
                horizontalLineTo(3.75f)
                curveTo(3.551f, 21f, 3.36f, 20.921f, 3.22f, 20.78f)
                curveTo(3.079f, 20.64f, 3f, 20.449f, 3f, 20.25f)
                verticalLineTo(11.25f)
                curveTo(3f, 10.852f, 3.158f, 10.471f, 3.44f, 10.19f)
                lineTo(10.94f, 2.69f)
                curveTo(11.221f, 2.409f, 11.602f, 2.251f, 12f, 2.251f)
                curveTo(12.398f, 2.251f, 12.779f, 2.409f, 13.06f, 2.69f)
                lineTo(20.56f, 10.19f)
                curveTo(20.842f, 10.471f, 21f, 10.852f, 21f, 11.25f)
                close()
            }
        }.build()

        return _Home!!
    }

@Suppress("ObjectPropertyName")
private var _Home: ImageVector? = null
