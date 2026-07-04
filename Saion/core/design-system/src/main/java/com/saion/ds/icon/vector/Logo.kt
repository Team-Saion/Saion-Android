package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcKakaoLogo: ImageVector
    get() {
        if (_KakaoLogo != null) {
            return _KakaoLogo!!
        }
        _KakaoLogo = ImageVector.Builder(
            name = "Logo",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 20f,
            viewportHeight = 20f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF1A1E26)),
                pathFillType = PathFillType.EvenOdd,
            ) {
                moveTo(10f, 2.629f)
                curveTo(5.663f, 2.629f, 2.143f, 5.363f, 2.143f, 8.726f)
                curveTo(2.143f, 10.823f, 3.502f, 12.654f, 5.577f, 13.77f)
                lineTo(4.705f, 16.968f)
                curveTo(4.688f, 17.031f, 4.692f, 17.099f, 4.714f, 17.16f)
                curveTo(4.737f, 17.222f, 4.778f, 17.275f, 4.832f, 17.313f)
                curveTo(4.886f, 17.35f, 4.95f, 17.371f, 5.016f, 17.371f)
                curveTo(5.081f, 17.371f, 5.146f, 17.351f, 5.2f, 17.313f)
                lineTo(9.018f, 14.776f)
                curveTo(9.34f, 14.776f, 9.67f, 14.831f, 10f, 14.831f)
                curveTo(14.337f, 14.831f, 17.857f, 12.096f, 17.857f, 8.726f)
                curveTo(17.857f, 5.355f, 14.337f, 2.629f, 10f, 2.629f)
                close()
            }
        }.build()

        return _KakaoLogo!!
    }

@Suppress("ObjectPropertyName")
private var _KakaoLogo: ImageVector? = null
