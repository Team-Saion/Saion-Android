package com.saion.ds.component.button

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.LocalSaionColors
import com.saion.ds.theme.LocalSaionRadius
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

enum class TextButtonVariant {
    NORMAL,
    CHEVRON,
}

enum class TextButtonSize {
    LARGE,
    MEDIUM,
    SMALL,
}

@Composable
fun SaionTextButton(
    text: String,
    modifier: Modifier = Modifier,
    variant: TextButtonVariant = TextButtonVariant.NORMAL,
    size: TextButtonSize = TextButtonSize.MEDIUM,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
) {
    val styleState = rememberUpdatedStyleState(interactionSource) { state ->
        state.isEnabled = enabled
    }

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick,
            )
            .styleable(
                styleState = styleState,
                buttonBaseStyle(variant = variant),
                buttonInteractionStyle(),
            ),
        horizontalArrangement = Arrangement.spacedBy(
            space = SaionTheme.spacing.v4,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides if (enabled) SaionTheme.colors.label.strong else SaionTheme.colors.label.disabled,
        ) {
            Text(text = text, style = size.textStyle)
            if (variant == TextButtonVariant.CHEVRON) {
                Icon(
                    imageVector = SaionIcons.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(size = size.iconSize),
                )
            }
        }
    }
}

@Composable
private fun buttonBaseStyle(variant: TextButtonVariant): Style = Style {
    shape(LocalSaionRadius.currentValue.component.small.toRoundedCornerShape())
    clip()

    contentPaddingStart(6.dp)
    contentPaddingEnd(if (variant == TextButtonVariant.NORMAL) 6.dp else 2.dp)
    contentPaddingVertical(2.dp)
}

@Composable
private fun buttonInteractionStyle(): Style = Style {
    val colors = LocalSaionColors.currentValue

    pressed {
        animate(tween(100)) {
            foreground(SolidColor(colors.overlay.pressedSubtle))
            scale(0.96f)
        }
    }
}

private val TextButtonSize.textStyle: TextStyle
    @Composable
    get() = when (this) {
        TextButtonSize.LARGE -> SaionTheme.typography.title1Subtle
        TextButtonSize.MEDIUM -> SaionTheme.typography.body1
        TextButtonSize.SMALL -> SaionTheme.typography.label1Subtle
    }

private val TextButtonSize.iconSize: Dp
    @Composable
    get() = when (this) {
        TextButtonSize.LARGE -> 14.dp
        TextButtonSize.MEDIUM -> 12.dp
        TextButtonSize.SMALL -> 10.dp
    }

@Preview(showBackground = true, name = "Normal")
@Composable
private fun SaionTextButtonNormalPreview() {
    SaionTheme {
        TextButtonVariantPreviewContent(variant = TextButtonVariant.NORMAL)
    }
}

@Preview(showBackground = true, name = "Chevron")
@Composable
private fun SaionTextButtonChevronPreview() {
    SaionTheme {
        TextButtonVariantPreviewContent(variant = TextButtonVariant.CHEVRON)
    }
}

@Composable
private fun TextButtonVariantPreviewContent(variant: TextButtonVariant) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(2) { time ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButtonSize.entries.forEach { size ->
                    SaionTextButton(
                        text = "Label",
                        variant = variant,
                        size = size,
                        enabled = time % 2 == 0,
                        onClick = {},
                    )
                }
            }
        }
    }
}
