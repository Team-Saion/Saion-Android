package com.saion.ds.token.radius

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
data class SemanticRadius(
    val component: Component,
    val container: Container,
) {
    @Immutable
    data class Component(
        val small: Dp,
        val medium: Dp,
        val large: Dp,
        val full: Dp,
        val xSmall: Dp,
        val xLarge: Dp,
        val xxLarge: Dp,
    )

    @Immutable
    data class Container(
        val small: Dp,
        val medium: Dp,
        val large: Dp,
        val xLarge: Dp,
        val xxLarge: Dp,
    )
}

internal fun createSemanticRadiusDefault(): SemanticRadius = SemanticRadius(
    component = SemanticRadius.Component(
        small = PrimitiveRadius.V6,
        medium = PrimitiveRadius.V8,
        large = PrimitiveRadius.V10,
        full = PrimitiveRadius.V999,
        xSmall = PrimitiveRadius.V4,
        xLarge = PrimitiveRadius.V12,
        xxLarge = PrimitiveRadius.V16,
    ),
    container = SemanticRadius.Container(
        small = PrimitiveRadius.V12,
        medium = PrimitiveRadius.V16,
        large = PrimitiveRadius.V20,
        xLarge = PrimitiveRadius.V24,
        xxLarge = PrimitiveRadius.V28,
    ),
)

fun Dp.toRoundedCornerShape(): RoundedCornerShape = RoundedCornerShape(size = this)
