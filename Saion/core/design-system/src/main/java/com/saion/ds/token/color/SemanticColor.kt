package com.saion.ds.token.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class SemanticColor(
    val primary: Primary,
    val label: Label,
    val background: Background,
    val line: Line,
    val status: Status,
    val fill: Fill,
    val overlay: Overlay,
) {
    @Immutable
    data class Primary(
        val default: Color,
        val subtle: Color,
        val strong: Color,
    )

    @Immutable
    data class Label(
        val default: Color,
        val strong: Color,
        val subtle: Color,
        val disabled: Color,
    )

    @Immutable
    data class Background(
        val default: Color,
        val subtle: Color,
        val muted: Color,
    )

    @Immutable
    data class Line(
        val default: Color,
        val strong: Color,
        val subtle: Color,
    )

    @Immutable
    data class Status(
        val positive: Positive,
        val cautionary: Cautionary,
        val negative: Negative,
    ) {
        @Immutable
        data class Positive(
            val default: Color,
            val subtle: Color,
        )

        @Immutable
        data class Cautionary(
            val default: Color,
            val subtle: Color,
        )

        @Immutable
        data class Negative(
            val default: Color,
            val subtle: Color,
        )
    }

    @Immutable
    data class Fill(
        val default: Color,
        val subtle: Color,
        val disabled: Color,
    )

    @Immutable
    data class Overlay(
        val dimmer: Color,
        val pressed: Color,
        val disabled: Color,
        val pressedSubtle: Color,
    )
}

internal fun createSemanticColorLight(): SemanticColor = SemanticColor(
    primary = SemanticColor.Primary(
        default = PrimitiveColor.Grey900,
        subtle = PrimitiveColor.Grey400,
        strong = PrimitiveColor.Common100,
    ),
    label = SemanticColor.Label(
        default = PrimitiveColor.Grey900,
        strong = PrimitiveColor.Grey800,
        subtle = PrimitiveColor.Grey600,
        disabled = PrimitiveColor.Grey400,
    ),
    background = SemanticColor.Background(
        default = PrimitiveColor.Common0,
        subtle = PrimitiveColor.Grey50,
        muted = PrimitiveColor.Grey100,
    ),
    line = SemanticColor.Line(
        default = PrimitiveColor.Grey200,
        strong = PrimitiveColor.Grey300,
        subtle = PrimitiveColor.Grey100,
    ),
    status = SemanticColor.Status(
        positive = SemanticColor.Status.Positive(
            default = PrimitiveColor.Green500,
            subtle = PrimitiveColor.Green50,
        ),
        cautionary = SemanticColor.Status.Cautionary(
            default = PrimitiveColor.Orange500,
            subtle = PrimitiveColor.Orange50,
        ),
        negative = SemanticColor.Status.Negative(
            default = PrimitiveColor.Red500,
            subtle = PrimitiveColor.Red50,
        ),
    ),
    fill = SemanticColor.Fill(
        default = PrimitiveColor.Grey100,
        subtle = PrimitiveColor.Grey50,
        disabled = PrimitiveColor.Grey200,
    ),
    overlay = SemanticColor.Overlay(
        dimmer = PrimitiveColor.Opacity28,
        pressed = PrimitiveColor.Opacity12,
        disabled = PrimitiveColor.OpacityWhite76,
        pressedSubtle = PrimitiveColor.Opacity4,
    ),
)
