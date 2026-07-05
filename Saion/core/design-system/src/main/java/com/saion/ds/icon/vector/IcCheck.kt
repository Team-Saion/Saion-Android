package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcCheck: ImageVector
    get() {
        if (_Check != null) {
            return _Check!!
        }
        _Check = ImageVector.Builder(
            name = "Check",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF6C757F)),
                pathFillType = PathFillType.EvenOdd,
            ) {
                moveTo(21.309f, 5.913f)
                curveTo(21.896f, 6.498f, 21.897f, 7.448f, 21.312f, 8.034f)
                lineTo(11.287f, 18.084f)
                curveTo(11.005f, 18.367f, 10.623f, 18.525f, 10.224f, 18.525f)
                curveTo(9.826f, 18.525f, 9.444f, 18.366f, 9.162f, 18.084f)
                lineTo(2.537f, 11.434f)
                curveTo(1.953f, 10.847f, 1.954f, 9.897f, 2.541f, 9.312f)
                curveTo(3.128f, 8.728f, 4.078f, 8.729f, 4.663f, 9.316f)
                lineTo(10.226f, 14.9f)
                lineTo(19.188f, 5.916f)
                curveTo(19.773f, 5.329f, 20.723f, 5.328f, 21.309f, 5.913f)
                close()
            }
        }.build()

        return _Check!!
    }

@Suppress("ObjectPropertyName")
private var _Check: ImageVector? = null
