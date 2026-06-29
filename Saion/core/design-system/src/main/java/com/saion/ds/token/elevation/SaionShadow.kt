package com.saion.ds.token.elevation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.saion.ds.token.color.PrimitiveColor

@Immutable
data class SaionShadow(
    val container: Shadow,
    val component: Shadow,
)

internal fun createSaionShadow(): SaionShadow = SaionShadow(
    container = Shadow(
        radius = 10.dp,
        spread = 0.dp,
        color = PrimitiveColor.Opacity8,
        offset = DpOffset(x = 0.dp, y = 4.dp),
    ),
    component = Shadow(
        radius = 24.dp,
        spread = 0.dp,
        color = PrimitiveColor.Opacity12,
        offset = DpOffset.Zero,
    ),
)
