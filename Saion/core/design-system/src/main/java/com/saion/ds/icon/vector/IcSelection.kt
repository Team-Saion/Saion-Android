package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val IcSelection: ImageVector
    get() {
        if (_Selection != null) {
            return _Selection!!
        }
        _Selection = ImageVector
            .Builder(
                name = "Selection",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF6C757F))) {
                    moveTo(14.25f, 3.75f)
                    curveTo(14.25f, 3.949f, 14.171f, 4.14f, 14.03f, 4.28f)
                    curveTo(13.89f, 4.421f, 13.699f, 4.5f, 13.5f, 4.5f)
                    horizontalLineTo(10.5f)
                    curveTo(10.301f, 4.5f, 10.11f, 4.421f, 9.97f, 4.28f)
                    curveTo(9.829f, 4.14f, 9.75f, 3.949f, 9.75f, 3.75f)
                    curveTo(9.75f, 3.551f, 9.829f, 3.36f, 9.97f, 3.22f)
                    curveTo(10.11f, 3.079f, 10.301f, 3f, 10.5f, 3f)
                    horizontalLineTo(13.5f)
                    curveTo(13.699f, 3f, 13.89f, 3.079f, 14.03f, 3.22f)
                    curveTo(14.171f, 3.36f, 14.25f, 3.551f, 14.25f, 3.75f)
                    close()
                    moveTo(13.5f, 19.5f)
                    horizontalLineTo(10.5f)
                    curveTo(10.301f, 19.5f, 10.11f, 19.579f, 9.97f, 19.72f)
                    curveTo(9.829f, 19.86f, 9.75f, 20.051f, 9.75f, 20.25f)
                    curveTo(9.75f, 20.449f, 9.829f, 20.64f, 9.97f, 20.78f)
                    curveTo(10.11f, 20.921f, 10.301f, 21f, 10.5f, 21f)
                    horizontalLineTo(13.5f)
                    curveTo(13.699f, 21f, 13.89f, 20.921f, 14.03f, 20.78f)
                    curveTo(14.171f, 20.64f, 14.25f, 20.449f, 14.25f, 20.25f)
                    curveTo(14.25f, 20.051f, 14.171f, 19.86f, 14.03f, 19.72f)
                    curveTo(13.89f, 19.579f, 13.699f, 19.5f, 13.5f, 19.5f)
                    close()
                    moveTo(19.5f, 3f)
                    horizontalLineTo(17.25f)
                    curveTo(17.051f, 3f, 16.86f, 3.079f, 16.72f, 3.22f)
                    curveTo(16.579f, 3.36f, 16.5f, 3.551f, 16.5f, 3.75f)
                    curveTo(16.5f, 3.949f, 16.579f, 4.14f, 16.72f, 4.28f)
                    curveTo(16.86f, 4.421f, 17.051f, 4.5f, 17.25f, 4.5f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(6.75f)
                    curveTo(19.5f, 6.949f, 19.579f, 7.14f, 19.72f, 7.28f)
                    curveTo(19.86f, 7.421f, 20.051f, 7.5f, 20.25f, 7.5f)
                    curveTo(20.449f, 7.5f, 20.64f, 7.421f, 20.78f, 7.28f)
                    curveTo(20.921f, 7.14f, 21f, 6.949f, 21f, 6.75f)
                    verticalLineTo(4.5f)
                    curveTo(21f, 4.102f, 20.842f, 3.721f, 20.561f, 3.439f)
                    curveTo(20.279f, 3.158f, 19.898f, 3f, 19.5f, 3f)
                    close()
                    moveTo(20.25f, 9.75f)
                    curveTo(20.051f, 9.75f, 19.86f, 9.829f, 19.72f, 9.97f)
                    curveTo(19.579f, 10.11f, 19.5f, 10.301f, 19.5f, 10.5f)
                    verticalLineTo(13.5f)
                    curveTo(19.5f, 13.699f, 19.579f, 13.89f, 19.72f, 14.03f)
                    curveTo(19.86f, 14.171f, 20.051f, 14.25f, 20.25f, 14.25f)
                    curveTo(20.449f, 14.25f, 20.64f, 14.171f, 20.78f, 14.03f)
                    curveTo(20.921f, 13.89f, 21f, 13.699f, 21f, 13.5f)
                    verticalLineTo(10.5f)
                    curveTo(21f, 10.301f, 20.921f, 10.11f, 20.78f, 9.97f)
                    curveTo(20.64f, 9.829f, 20.449f, 9.75f, 20.25f, 9.75f)
                    close()
                    moveTo(20.25f, 16.5f)
                    curveTo(20.051f, 16.5f, 19.86f, 16.579f, 19.72f, 16.72f)
                    curveTo(19.579f, 16.86f, 19.5f, 17.051f, 19.5f, 17.25f)
                    verticalLineTo(19.5f)
                    horizontalLineTo(17.25f)
                    curveTo(17.051f, 19.5f, 16.86f, 19.579f, 16.72f, 19.72f)
                    curveTo(16.579f, 19.86f, 16.5f, 20.051f, 16.5f, 20.25f)
                    curveTo(16.5f, 20.449f, 16.579f, 20.64f, 16.72f, 20.78f)
                    curveTo(16.86f, 20.921f, 17.051f, 21f, 17.25f, 21f)
                    horizontalLineTo(19.5f)
                    curveTo(19.898f, 21f, 20.279f, 20.842f, 20.561f, 20.561f)
                    curveTo(20.842f, 20.279f, 21f, 19.898f, 21f, 19.5f)
                    verticalLineTo(17.25f)
                    curveTo(21f, 17.051f, 20.921f, 16.86f, 20.78f, 16.72f)
                    curveTo(20.64f, 16.579f, 20.449f, 16.5f, 20.25f, 16.5f)
                    close()
                    moveTo(3.75f, 14.25f)
                    curveTo(3.949f, 14.25f, 4.14f, 14.171f, 4.28f, 14.03f)
                    curveTo(4.421f, 13.89f, 4.5f, 13.699f, 4.5f, 13.5f)
                    verticalLineTo(10.5f)
                    curveTo(4.5f, 10.301f, 4.421f, 10.11f, 4.28f, 9.97f)
                    curveTo(4.14f, 9.829f, 3.949f, 9.75f, 3.75f, 9.75f)
                    curveTo(3.551f, 9.75f, 3.36f, 9.829f, 3.22f, 9.97f)
                    curveTo(3.079f, 10.11f, 3f, 10.301f, 3f, 10.5f)
                    verticalLineTo(13.5f)
                    curveTo(3f, 13.699f, 3.079f, 13.89f, 3.22f, 14.03f)
                    curveTo(3.36f, 14.171f, 3.551f, 14.25f, 3.75f, 14.25f)
                    close()
                    moveTo(6.75f, 19.5f)
                    horizontalLineTo(4.5f)
                    verticalLineTo(17.25f)
                    curveTo(4.5f, 17.051f, 4.421f, 16.86f, 4.28f, 16.72f)
                    curveTo(4.14f, 16.579f, 3.949f, 16.5f, 3.75f, 16.5f)
                    curveTo(3.551f, 16.5f, 3.36f, 16.579f, 3.22f, 16.72f)
                    curveTo(3.079f, 16.86f, 3f, 17.051f, 3f, 17.25f)
                    verticalLineTo(19.5f)
                    curveTo(3f, 19.898f, 3.158f, 20.279f, 3.439f, 20.561f)
                    curveTo(3.721f, 20.842f, 4.102f, 21f, 4.5f, 21f)
                    horizontalLineTo(6.75f)
                    curveTo(6.949f, 21f, 7.14f, 20.921f, 7.28f, 20.78f)
                    curveTo(7.421f, 20.64f, 7.5f, 20.449f, 7.5f, 20.25f)
                    curveTo(7.5f, 20.051f, 7.421f, 19.86f, 7.28f, 19.72f)
                    curveTo(7.14f, 19.579f, 6.949f, 19.5f, 6.75f, 19.5f)
                    close()
                    moveTo(6.75f, 3f)
                    horizontalLineTo(4.5f)
                    curveTo(4.102f, 3f, 3.721f, 3.158f, 3.439f, 3.439f)
                    curveTo(3.158f, 3.721f, 3f, 4.102f, 3f, 4.5f)
                    verticalLineTo(6.75f)
                    curveTo(3f, 6.949f, 3.079f, 7.14f, 3.22f, 7.28f)
                    curveTo(3.36f, 7.421f, 3.551f, 7.5f, 3.75f, 7.5f)
                    curveTo(3.949f, 7.5f, 4.14f, 7.421f, 4.28f, 7.28f)
                    curveTo(4.421f, 7.14f, 4.5f, 6.949f, 4.5f, 6.75f)
                    verticalLineTo(4.5f)
                    horizontalLineTo(6.75f)
                    curveTo(6.949f, 4.5f, 7.14f, 4.421f, 7.28f, 4.28f)
                    curveTo(7.421f, 4.14f, 7.5f, 3.949f, 7.5f, 3.75f)
                    curveTo(7.5f, 3.551f, 7.421f, 3.36f, 7.28f, 3.22f)
                    curveTo(7.14f, 3.079f, 6.949f, 3f, 6.75f, 3f)
                    close()
                }
            }.build()

        return _Selection!!
    }

@Suppress("ObjectPropertyName")
private var _Selection: ImageVector? = null
