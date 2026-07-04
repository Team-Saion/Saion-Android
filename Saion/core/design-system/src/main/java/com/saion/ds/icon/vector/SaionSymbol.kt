package com.saion.ds.icon.vector

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val SaionSymbol: ImageVector
    get() {
        if (_SaionSymbol != null) {
            return _SaionSymbol!!
        }
        _SaionSymbol = ImageVector.Builder(
            name = "SaionSymbol",
            defaultWidth = 100.dp,
            defaultHeight = 100.dp,
            viewportWidth = 100f,
            viewportHeight = 100f,
        ).apply {
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color(0xFF6C757F),
                        1f to Color(0xFF343C48),
                    ),
                    start = Offset(90f, 10.04f),
                    end = Offset(10.08f, 89.96f),
                ),
            ) {
                moveTo(50.08f, 17.2f)
                curveTo(67.1f, -0.06f, 94.77f, 16.26f, 89.29f, 39.31f)
                curveTo(88.35f, 43.29f, 85.72f, 47.35f, 82.89f, 50.24f)
                curveTo(86.08f, 52.49f, 88.81f, 58.1f, 89.51f, 61.97f)
                curveTo(91.91f, 75.21f, 83.97f, 87.46f, 70.21f, 89.71f)
                curveTo(64.17f, 90.68f, 57.99f, 89.2f, 53.03f, 85.61f)
                curveTo(51.96f, 84.85f, 50.94f, 84.01f, 49.98f, 83.11f)
                curveTo(49.64f, 83.46f, 49.29f, 83.8f, 48.94f, 84.13f)
                curveTo(44.35f, 88.19f, 38.35f, 90.28f, 32.23f, 89.96f)
                curveTo(26.02f, 89.7f, 20.17f, 86.94f, 16.02f, 82.31f)
                curveTo(7.85f, 73.25f, 8.04f, 58.3f, 17.18f, 50.06f)
                curveTo(14.91f, 48.45f, 12.07f, 43.61f, 11.28f, 41.07f)
                curveTo(4.42f, 19.09f, 28.41f, 1.3f, 47.33f, 14.66f)
                curveTo(48.37f, 15.39f, 49.04f, 16.43f, 49.99f, 17.13f)
                lineTo(50.08f, 17.2f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.3f to Color(0xFFFFE2B3),
                        1f to Color(0xFFFB8585),
                    ),
                    start = Offset(13.24f, 10.04f),
                    end = Offset(53.28f, 50.07f),
                ),
            ) {
                moveTo(17.11f, 50.07f)
                curveTo(14.83f, 48.46f, 11.99f, 43.62f, 11.2f, 41.08f)
                curveTo(4.34f, 19.09f, 28.34f, 1.28f, 47.27f, 14.65f)
                curveTo(48.31f, 15.38f, 48.98f, 16.42f, 49.93f, 17.12f)
                lineTo(50.02f, 17.19f)
                curveTo(51.78f, 18.62f, 54.44f, 23.1f, 55.3f, 25.59f)
                curveTo(57.88f, 33.1f, 56.85f, 42.26f, 47.42f, 43.47f)
                curveTo(43.2f, 44.01f, 37.91f, 43.22f, 33.61f, 43.56f)
                curveTo(29.24f, 43.59f, 26.57f, 43.87f, 22.66f, 46.01f)
                curveTo(20.06f, 47.42f, 19.18f, 47.95f, 17.11f, 50.07f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color(0xFFFFBF55),
                        0.8f to Color(0xFFFB8585),
                    ),
                    start = Offset(89.93f, 81.12f),
                    end = Offset(43.38f, 59.09f),
                ),
            ) {
                moveTo(82.87f, 50.22f)
                curveTo(86.06f, 52.47f, 88.8f, 58.09f, 89.5f, 61.95f)
                curveTo(91.9f, 75.2f, 83.95f, 87.46f, 70.19f, 89.71f)
                curveTo(64.14f, 90.68f, 57.96f, 89.2f, 53f, 85.61f)
                curveTo(51.93f, 84.85f, 50.91f, 84.01f, 49.95f, 83.1f)
                curveTo(46.4f, 78.64f, 44.87f, 76.62f, 43.81f, 70.92f)
                curveTo(42.28f, 64.97f, 44.87f, 57.77f, 51.67f, 56.95f)
                curveTo(57.79f, 56.21f, 64.86f, 57.5f, 71.12f, 56.27f)
                curveTo(76.33f, 55.25f, 78.86f, 53.56f, 82.87f, 50.22f)
                close()
            }
        }.build()

        return _SaionSymbol!!
    }

@Suppress("ObjectPropertyName")
private var _SaionSymbol: ImageVector? = null
