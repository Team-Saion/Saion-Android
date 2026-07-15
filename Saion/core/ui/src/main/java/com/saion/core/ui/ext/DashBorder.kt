package com.saion.core.ui.ext

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 지정한 [Shape] 외곽에 점선 보더를 그립니다.
 *
 * 단색 점선 보더가 필요할 때 [Brush] 대신 사용할 수 있는 편의 overload입니다.
 */
fun Modifier.dashBorder(
    width: Dp,
    color: Color,
    shape: Shape = RectangleShape,
    on: Dp,
    off: Dp,
    phase: Dp = 0.dp,
): Modifier = dashBorder(
    width = width,
    brush = Brush.linearGradient(listOf(color, color)),
    shape = shape,
    on = on,
    off = off,
    phase = phase,
)

/**
 * 지정한 [Shape] 외곽에 점선 보더를 그립니다.
 *
 * 스트로크는 컴포저블 경계 안쪽으로 반 픽셀만큼 inset 되어 그려지므로,
 * 일반 `border()`와 비슷하게 레이아웃 바깥으로 번지지 않도록 맞춥니다.
 *
 * @param width 점선 보더 두께입니다. `0.dp` 이하면 아무것도 그리지 않습니다.
 * @param brush 점선 보더에 사용할 브러시입니다.
 * @param shape 점선 경로를 만들 도형입니다.
 * @param on 점선이 보이는 구간 길이입니다.
 * @param off 점선 사이의 빈 구간 길이입니다.
 * @param phase 점선 패턴 시작 위치를 이동할 때 사용하는 오프셋입니다.
 */
fun Modifier.dashBorder(
    width: Dp,
    brush: Brush,
    shape: Shape = RectangleShape,
    on: Dp,
    off: Dp,
    phase: Dp = 0.dp,
): Modifier = drawWithCache {
    val strokeWidthPx = width.toPx()
    if (strokeWidthPx <= 0f) {
        onDrawWithContent { drawContent() }
    } else {
        val insetPx = strokeWidthPx / 2f
        val outlineSize = Size(
            width = (size.width - strokeWidthPx).coerceAtLeast(0f),
            height = (size.height - strokeWidthPx).coerceAtLeast(0f),
        )
        val outline = shape.createOutline(
            size = outlineSize,
            layoutDirection = layoutDirection,
            density = this,
        )
        val stroke = Stroke(
            width = strokeWidthPx,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(on.toPx(), off.toPx()),
                phase = phase.toPx(),
            ),
        )

        onDrawWithContent {
            drawContent()
            inset(horizontal = insetPx, vertical = insetPx) {
                when (outline) {
                    is Outline.Generic -> drawPath(
                        path = outline.path,
                        brush = brush,
                        style = stroke,
                    )

                    is Outline.Rectangle -> drawRect(
                        brush = brush,
                        topLeft = outline.rect.topLeft,
                        size = outline.rect.size,
                        style = stroke,
                    )

                    is Outline.Rounded -> drawPath(
                        path = Path().apply {
                            addRoundRect(outline.roundRect)
                        },
                        brush = brush,
                        style = stroke,
                    )
                }
            }
        }
    }
}
