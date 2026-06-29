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
            defaultWidth = 14.dp,
            defaultHeight = 14.dp,
            viewportWidth = 14f,
            viewportHeight = 14f,
        ).apply {
            path(fill = SolidColor(Color(0xFF343C48))) {
                moveTo(10.089f, 7.464f)
                lineTo(5.714f, 11.839f)
                curveTo(5.591f, 11.963f, 5.424f, 12.032f, 5.249f, 12.032f)
                curveTo(5.075f, 12.032f, 4.908f, 11.963f, 4.785f, 11.839f)
                curveTo(4.661f, 11.716f, 4.592f, 11.549f, 4.592f, 11.375f)
                curveTo(4.592f, 11.2f, 4.661f, 11.033f, 4.785f, 10.91f)
                lineTo(8.695f, 7f)
                lineTo(4.786f, 3.089f)
                curveTo(4.725f, 3.028f, 4.676f, 2.956f, 4.643f, 2.876f)
                curveTo(4.61f, 2.796f, 4.593f, 2.711f, 4.593f, 2.624f)
                curveTo(4.593f, 2.538f, 4.61f, 2.453f, 4.643f, 2.373f)
                curveTo(4.676f, 2.293f, 4.725f, 2.221f, 4.786f, 2.16f)
                curveTo(4.847f, 2.099f, 4.919f, 2.05f, 4.999f, 2.017f)
                curveTo(5.079f, 1.984f, 5.164f, 1.967f, 5.251f, 1.967f)
                curveTo(5.337f, 1.967f, 5.422f, 1.984f, 5.502f, 2.017f)
                curveTo(5.582f, 2.05f, 5.654f, 2.099f, 5.715f, 2.16f)
                lineTo(10.09f, 6.535f)
                curveTo(10.151f, 6.596f, 10.2f, 6.668f, 10.233f, 6.748f)
                curveTo(10.266f, 6.828f, 10.283f, 6.913f, 10.283f, 7f)
                curveTo(10.283f, 7.086f, 10.266f, 7.172f, 10.232f, 7.251f)
                curveTo(10.199f, 7.331f, 10.151f, 7.403f, 10.089f, 7.464f)
                close()
            }
        }.build()

        return _ChevronRight!!
    }

@Suppress("ObjectPropertyName")
private var _ChevronRight: ImageVector? = null
