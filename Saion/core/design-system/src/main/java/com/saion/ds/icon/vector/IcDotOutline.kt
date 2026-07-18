package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcDotOutline: ImageVector
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
                moveTo(12f, 9f)
                curveTo(11.407f, 9f, 10.827f, 9.176f, 10.333f, 9.506f)
                curveTo(9.84f, 9.835f, 9.455f, 10.304f, 9.228f, 10.852f)
                curveTo(9.001f, 11.4f, 8.942f, 12.003f, 9.058f, 12.585f)
                curveTo(9.173f, 13.167f, 9.459f, 13.702f, 9.879f, 14.121f)
                curveTo(10.298f, 14.541f, 10.833f, 14.827f, 11.415f, 14.942f)
                curveTo(11.997f, 15.058f, 12.6f, 14.999f, 13.148f, 14.772f)
                curveTo(13.696f, 14.545f, 14.165f, 14.16f, 14.494f, 13.667f)
                curveTo(14.824f, 13.173f, 15f, 12.593f, 15f, 12f)
                curveTo(15f, 11.204f, 14.684f, 10.441f, 14.121f, 9.879f)
                curveTo(13.559f, 9.316f, 12.796f, 9f, 12f, 9f)
                close()
                moveTo(12f, 13.5f)
                curveTo(11.703f, 13.5f, 11.413f, 13.412f, 11.167f, 13.247f)
                curveTo(10.92f, 13.082f, 10.728f, 12.848f, 10.614f, 12.574f)
                curveTo(10.501f, 12.3f, 10.471f, 11.998f, 10.529f, 11.707f)
                curveTo(10.587f, 11.416f, 10.73f, 11.149f, 10.939f, 10.939f)
                curveTo(11.149f, 10.73f, 11.416f, 10.587f, 11.707f, 10.529f)
                curveTo(11.998f, 10.471f, 12.3f, 10.501f, 12.574f, 10.614f)
                curveTo(12.848f, 10.728f, 13.082f, 10.92f, 13.247f, 11.167f)
                curveTo(13.412f, 11.413f, 13.5f, 11.703f, 13.5f, 12f)
                curveTo(13.5f, 12.398f, 13.342f, 12.779f, 13.061f, 13.061f)
                curveTo(12.779f, 13.342f, 12.398f, 13.5f, 12f, 13.5f)
                close()
            }
        }.build()

        return _DotOutline!!
    }

@Suppress("ObjectPropertyName")
private var _DotOutline: ImageVector? = null
