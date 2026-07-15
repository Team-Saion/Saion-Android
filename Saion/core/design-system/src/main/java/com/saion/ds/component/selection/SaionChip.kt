package com.saion.ds.component.selection

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.LocalSaionColors
import com.saion.ds.theme.LocalSaionRadius
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

enum class ChipShape {
    PILL,
    SQUARE,
}

enum class ChipSize {
    SMALL,
    MEDIUM,
}

@Composable
fun SaionChip(
    text: String,
    shape: ChipShape,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    size: ChipSize = ChipSize.MEDIUM,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
) {
    val styleState = rememberUpdatedStyleState(interactionSource = interactionSource)
    val colors = chipColors(isSelected = isSelected)
    val chipShape = shape.toRoundedCornerShape()

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .height(size.height)
            .clip(shape = chipShape)
            .background(
                color = colors.background,
                shape = chipShape,
            )
            .border(
                width = 1.dp,
                color = SaionTheme.colors.line.subtle,
                shape = chipShape,
            )
            .styleable(
                styleState = styleState,
                chipInteractionStyle(),
            )
            .padding(horizontal = size.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.let { ChipIconSlot(content = it, placeAfterContent = false) }

        Text(
            text = text,
            style = size.textStyle,
            color = colors.content,
        )

        trailingIcon?.let { ChipIconSlot(content = it, placeAfterContent = true) }
    }
}

@Composable
private fun chipInteractionStyle(): Style = Style {
    val colors = LocalSaionColors.currentValue

    pressed {
        animate(tween(100)) {
            foreground(SolidColor(colors.overlay.pressedSubtle))
        }
    }
}

@Composable
private fun chipColors(isSelected: Boolean): ChipColors = if (isSelected) {
    ChipColors(
        background = SaionTheme.colors.label.strong,
        content = SaionTheme.colors.label.inverse,
    )
} else {
    ChipColors(
        background = SaionTheme.colors.background.default,
        content = SaionTheme.colors.label.strong,
    )
}

@Composable
private fun ChipIconSlot(
    content: @Composable () -> Unit,
    placeAfterContent: Boolean,
) {
    if (placeAfterContent) {
        Spacer(modifier = Modifier.width(4.dp))
        content()
    } else {
        content()
        Spacer(modifier = Modifier.width(4.dp))
    }
}

private data class ChipColors(
    val background: Color,
    val content: Color,
)

private val ChipSize.height: Dp
    get() = when (this) {
        ChipSize.SMALL -> 32.dp
        ChipSize.MEDIUM -> 38.dp
    }

private val ChipSize.horizontalPadding: Dp
    get() = when (this) {
        ChipSize.SMALL -> 10.dp
        ChipSize.MEDIUM -> 12.dp
    }

private val ChipSize.textStyle
    @Composable
    get() = when (this) {
        ChipSize.SMALL -> SaionTheme.typography.label2
        ChipSize.MEDIUM -> SaionTheme.typography.title3
    }

@Composable
private fun ChipShape.toRoundedCornerShape() = when (this) {
    ChipShape.PILL -> LocalSaionRadius.current.component.full
    ChipShape.SQUARE -> LocalSaionRadius.current.component.medium
}.toRoundedCornerShape()

@Preview(showBackground = true)
@Composable
private fun SaionChipPreview() {
    SaionTheme {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChipSize.entries.forEach { size ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipShape.entries.forEach { shape ->
                        SaionChip(
                            text = "Label",
                            shape = shape,
                            size = size,
                            onClick = {},
                        )
                        SaionChip(
                            text = "Label",
                            shape = shape,
                            size = size,
                            isSelected = true,
                            onClick = {},
                        )
                    }
                }
            }
        }
    }
}
