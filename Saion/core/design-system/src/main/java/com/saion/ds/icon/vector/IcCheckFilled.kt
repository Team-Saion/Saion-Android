package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcCheckFilled: ImageVector
    get() {
        if (_CheckFilled != null) {
            return _CheckFilled!!
        }
        _CheckFilled = ImageVector.Builder(
            name = "CheckFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF6C757F)),
                pathFillType = PathFillType.EvenOdd,
            ) {
                moveTo(16.615f, 10.418f)
                lineTo(11.667f, 15.367f)
                curveTo(11.5f, 15.533f, 11.275f, 15.626f, 11.04f, 15.626f)
                curveTo(10.805f, 15.626f, 10.579f, 15.533f, 10.413f, 15.367f)
                lineTo(7.385f, 12.34f)
                curveTo(7.302f, 12.257f, 7.237f, 12.16f, 7.193f, 12.052f)
                curveTo(7.148f, 11.944f, 7.125f, 11.829f, 7.125f, 11.713f)
                curveTo(7.125f, 11.478f, 7.219f, 11.252f, 7.385f, 11.086f)
                curveTo(7.551f, 10.92f, 7.776f, 10.827f, 8.011f, 10.827f)
                curveTo(8.246f, 10.827f, 8.472f, 10.92f, 8.638f, 11.086f)
                lineTo(11.039f, 13.487f)
                lineTo(15.362f, 9.165f)
                curveTo(15.444f, 9.082f, 15.542f, 9.017f, 15.649f, 8.972f)
                curveTo(15.757f, 8.928f, 15.872f, 8.905f, 15.989f, 8.905f)
                curveTo(16.105f, 8.905f, 16.22f, 8.928f, 16.328f, 8.972f)
                curveTo(16.435f, 9.017f, 16.533f, 9.082f, 16.615f, 9.165f)
                curveTo(16.698f, 9.247f, 16.763f, 9.345f, 16.807f, 9.452f)
                curveTo(16.852f, 9.56f, 16.875f, 9.675f, 16.875f, 9.791f)
                curveTo(16.875f, 9.908f, 16.852f, 10.023f, 16.807f, 10.13f)
                curveTo(16.763f, 10.238f, 16.698f, 10.335f, 16.615f, 10.418f)
                close()
                moveTo(12f, 2.25f)
                curveTo(6.615f, 2.25f, 2.25f, 6.615f, 2.25f, 12f)
                curveTo(2.25f, 17.385f, 6.615f, 21.75f, 12f, 21.75f)
                curveTo(17.385f, 21.75f, 21.75f, 17.385f, 21.75f, 12f)
                curveTo(21.75f, 6.615f, 17.385f, 2.25f, 12f, 2.25f)
                close()
            }
        }.build()

        return _CheckFilled!!
    }

@Suppress("ObjectPropertyName")
private var _CheckFilled: ImageVector? = null
