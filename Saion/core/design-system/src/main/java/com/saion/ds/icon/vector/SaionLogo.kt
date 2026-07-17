package com.saion.ds.icon.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val SaionLogo: ImageVector
    get() {
        if (_Logo != null) {
            return _Logo!!
        }
        _Logo = ImageVector.Builder(
            name = "Logo",
            defaultWidth = 115.dp,
            defaultHeight = 27.dp,
            viewportWidth = 115f,
            viewportHeight = 27f,
        ).apply {
            path(fill = SolidColor(Color(0xFF1A1E26))) {
                moveTo(54.6f, 27f)
                verticalLineTo(0f)
                horizontalLineTo(60.4f)
                verticalLineTo(27f)
                horizontalLineTo(54.6f)
                close()
                moveTo(42.2f, 0.32f)
                curveTo(45.05f, 0.32f, 47.29f, 1.38f, 48.92f, 3.52f)
                curveTo(50.55f, 5.65f, 51.36f, 8.87f, 51.36f, 13.18f)
                curveTo(51.36f, 17.5f, 50.55f, 20.72f, 48.92f, 22.85f)
                curveTo(47.29f, 24.98f, 45.05f, 26.05f, 42.2f, 26.05f)
                curveTo(39.32f, 26.05f, 37.07f, 24.98f, 35.44f, 22.85f)
                curveTo(33.81f, 20.72f, 33f, 17.5f, 33f, 13.18f)
                curveTo(33f, 8.87f, 33.81f, 5.65f, 35.44f, 3.52f)
                curveTo(37.07f, 1.38f, 39.32f, 0.32f, 42.2f, 0.32f)
                close()
                moveTo(42.2f, 5.2f)
                curveTo(39.88f, 5.2f, 38.72f, 7.86f, 38.72f, 13.18f)
                curveTo(38.72f, 18.5f, 39.88f, 21.16f, 42.2f, 21.16f)
                curveTo(44.52f, 21.16f, 45.68f, 18.5f, 45.68f, 13.18f)
                curveTo(45.68f, 10.35f, 45.36f, 8.31f, 44.72f, 7.07f)
                curveTo(44.11f, 5.82f, 43.27f, 5.2f, 42.2f, 5.2f)
                close()
            }
            path(fill = SolidColor(Color(0xFF1A1E26))) {
                moveTo(27.44f, 15.05f)
                verticalLineTo(27f)
                horizontalLineTo(21.68f)
                verticalLineTo(0f)
                horizontalLineTo(27.44f)
                verticalLineTo(9.88f)
                horizontalLineTo(30.88f)
                verticalLineTo(15.05f)
                horizontalLineTo(27.44f)
                close()
                moveTo(13.72f, 0.7f)
                curveTo(13.72f, 4.71f, 13.35f, 8.36f, 12.6f, 11.64f)
                lineTo(21.08f, 22.64f)
                lineTo(16.32f, 25.7f)
                lineTo(10.52f, 17.82f)
                curveTo(9.32f, 20.36f, 7.81f, 22.3f, 6f, 23.66f)
                curveTo(4.21f, 25.02f, 2.21f, 25.71f, 0f, 25.73f)
                verticalLineTo(20.25f)
                curveTo(1.55f, 20.09f, 2.91f, 19.15f, 4.08f, 17.44f)
                curveTo(5.25f, 15.73f, 6.17f, 13.42f, 6.84f, 10.51f)
                curveTo(7.51f, 7.58f, 7.84f, 4.25f, 7.84f, 0.53f)
                lineTo(13.72f, 0.7f)
                close()
            }
            path(fill = SolidColor(Color(0xFF1A1E26))) {
                moveTo(91f, 27f)
                verticalLineTo(4.61f)
                horizontalLineTo(96.6f)
                verticalLineTo(10.97f)
                lineTo(96.09f, 10.01f)
                curveTo(96.6f, 8.68f, 97.3f, 7.57f, 98.17f, 6.7f)
                curveTo(99.04f, 5.8f, 100.07f, 5.13f, 101.26f, 4.7f)
                curveTo(102.48f, 4.23f, 103.8f, 4f, 105.23f, 4f)
                curveTo(106.68f, 4f, 107.96f, 4.23f, 109.05f, 4.7f)
                curveTo(110.17f, 5.16f, 111.1f, 5.86f, 111.85f, 6.79f)
                curveTo(112.61f, 7.69f, 113.18f, 8.81f, 113.56f, 10.14f)
                curveTo(113.98f, 11.45f, 114.18f, 12.94f, 114.18f, 14.63f)
                verticalLineTo(27f)
                horizontalLineTo(108.54f)
                verticalLineTo(15.98f)
                curveTo(108.54f, 14.53f, 108.33f, 13.29f, 107.92f, 12.28f)
                curveTo(107.51f, 11.23f, 106.89f, 10.43f, 106.07f, 9.88f)
                curveTo(105.24f, 9.3f, 104.2f, 9.01f, 102.94f, 9.01f)
                curveTo(101.63f, 9.01f, 100.5f, 9.31f, 99.55f, 9.92f)
                curveTo(98.61f, 10.53f, 97.88f, 11.38f, 97.37f, 12.45f)
                curveTo(96.86f, 13.53f, 96.6f, 14.76f, 96.6f, 16.15f)
                verticalLineTo(27f)
                horizontalLineTo(91f)
                close()
            }
            path(fill = SolidColor(Color(0xFF1A1E26))) {
                moveTo(76.07f, 27f)
                curveTo(73.57f, 27f, 71.41f, 26.52f, 69.59f, 25.56f)
                curveTo(67.78f, 24.6f, 66.38f, 23.25f, 65.42f, 21.53f)
                curveTo(64.47f, 19.8f, 64f, 17.79f, 64f, 15.5f)
                curveTo(64f, 14.11f, 64.18f, 12.84f, 64.53f, 11.68f)
                curveTo(64.89f, 10.49f, 65.39f, 9.43f, 66.05f, 8.5f)
                curveTo(66.74f, 7.54f, 67.56f, 6.73f, 68.53f, 6.08f)
                curveTo(69.52f, 5.4f, 70.64f, 4.89f, 71.89f, 4.55f)
                curveTo(73.17f, 4.18f, 74.56f, 4f, 76.07f, 4f)
                curveTo(78.6f, 4f, 80.76f, 4.48f, 82.55f, 5.44f)
                curveTo(84.34f, 6.4f, 85.71f, 7.75f, 86.66f, 9.47f)
                curveTo(87.63f, 11.17f, 88.11f, 13.18f, 88.11f, 15.5f)
                curveTo(88.11f, 16.86f, 87.93f, 18.13f, 87.58f, 19.32f)
                curveTo(87.25f, 20.51f, 86.74f, 21.58f, 86.06f, 22.54f)
                curveTo(85.39f, 23.48f, 84.57f, 24.28f, 83.58f, 24.96f)
                curveTo(82.61f, 25.61f, 81.49f, 26.11f, 80.21f, 26.45f)
                curveTo(78.96f, 26.82f, 77.58f, 27f, 76.07f, 27f)
                close()
                moveTo(76.07f, 22.29f)
                curveTo(76.95f, 22.29f, 77.72f, 22.18f, 78.41f, 21.95f)
                curveTo(79.09f, 21.72f, 79.68f, 21.4f, 80.18f, 20.97f)
                curveTo(80.7f, 20.55f, 81.12f, 20.05f, 81.45f, 19.49f)
                curveTo(81.81f, 18.92f, 82.07f, 18.3f, 82.23f, 17.62f)
                curveTo(82.4f, 16.94f, 82.48f, 16.24f, 82.48f, 15.5f)
                curveTo(82.48f, 14.23f, 82.24f, 13.08f, 81.77f, 12.06f)
                curveTo(81.32f, 11.04f, 80.62f, 10.24f, 79.65f, 9.64f)
                curveTo(78.7f, 9.02f, 77.51f, 8.71f, 76.07f, 8.71f)
                curveTo(75.2f, 8.71f, 74.42f, 8.82f, 73.74f, 9.05f)
                curveTo(73.05f, 9.28f, 72.45f, 9.6f, 71.93f, 10.03f)
                curveTo(71.41f, 10.42f, 70.97f, 10.9f, 70.62f, 11.47f)
                curveTo(70.29f, 12.03f, 70.04f, 12.66f, 69.88f, 13.34f)
                curveTo(69.71f, 14.01f, 69.63f, 14.74f, 69.63f, 15.5f)
                curveTo(69.63f, 16.72f, 69.86f, 17.85f, 70.34f, 18.89f)
                curveTo(70.81f, 19.94f, 71.52f, 20.78f, 72.46f, 21.4f)
                curveTo(73.43f, 21.99f, 74.63f, 22.29f, 76.07f, 22.29f)
                close()
            }
        }.build()

        return _Logo!!
    }

@Suppress("ObjectPropertyName")
private var _Logo: ImageVector? = null
