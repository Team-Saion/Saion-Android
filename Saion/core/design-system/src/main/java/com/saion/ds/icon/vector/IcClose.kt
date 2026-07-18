package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcClose: ImageVector
    get() {
        if (_IcClose != null) {
            return _IcClose!!
        }
        _IcClose = ImageVector.Builder(
            name = "Close",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF6C757F))) {
                moveTo(19.546f, 17.954f)
                curveTo(19.757f, 18.165f, 19.876f, 18.452f, 19.876f, 18.751f)
                curveTo(19.876f, 19.05f, 19.757f, 19.337f, 19.546f, 19.548f)
                curveTo(19.335f, 19.759f, 19.048f, 19.878f, 18.749f, 19.878f)
                curveTo(18.45f, 19.878f, 18.163f, 19.759f, 17.952f, 19.548f)
                lineTo(12f, 13.594f)
                lineTo(6.046f, 19.546f)
                curveTo(5.835f, 19.757f, 5.548f, 19.876f, 5.249f, 19.876f)
                curveTo(4.95f, 19.876f, 4.664f, 19.757f, 4.452f, 19.546f)
                curveTo(4.241f, 19.335f, 4.122f, 19.048f, 4.122f, 18.749f)
                curveTo(4.122f, 18.45f, 4.241f, 18.164f, 4.452f, 17.952f)
                lineTo(10.406f, 12f)
                lineTo(4.454f, 6.046f)
                curveTo(4.243f, 5.835f, 4.124f, 5.548f, 4.124f, 5.249f)
                curveTo(4.124f, 4.95f, 4.243f, 4.664f, 4.454f, 4.452f)
                curveTo(4.665f, 4.241f, 4.952f, 4.122f, 5.251f, 4.122f)
                curveTo(5.55f, 4.122f, 5.836f, 4.241f, 6.048f, 4.452f)
                lineTo(12f, 10.406f)
                lineTo(17.954f, 4.451f)
                curveTo(18.165f, 4.24f, 18.452f, 4.121f, 18.751f, 4.121f)
                curveTo(19.05f, 4.121f, 19.337f, 4.24f, 19.548f, 4.451f)
                curveTo(19.759f, 4.663f, 19.878f, 4.949f, 19.878f, 5.248f)
                curveTo(19.878f, 5.547f, 19.759f, 5.834f, 19.548f, 6.045f)
                lineTo(13.594f, 12f)
                lineTo(19.546f, 17.954f)
                close()
            }
        }.build()

        return _IcClose!!
    }

@Suppress("ObjectPropertyName")
private var _IcClose: ImageVector? = null
