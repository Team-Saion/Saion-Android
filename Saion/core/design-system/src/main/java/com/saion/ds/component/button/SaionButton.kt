package com.saion.ds.component.button

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.disabled
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.LocalSaionColors
import com.saion.ds.theme.LocalSaionRadius
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.color.SemanticColor
import com.saion.ds.token.radius.toRoundedCornerShape

enum class ButtonVariant {
    PRIMARY,
    SUBTLE,
    NEUTRAL,
    DANGER,
}

enum class ButtonSize {
    XLARGE,
    LARGE,
    MEDIUM,
    SMALL,
}

@Composable
fun SaionButton(
    text: String,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
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
                buttonBaseStyle(size = size, variant = variant),
                buttonInteractionStyle(colors = SaionTheme.colors),
            ),
        horizontalArrangement = Arrangement.spacedBy(
            space = SaionTheme.spacing.v4,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides variant.contentColor) {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
            Text(text = text, style = size.textStyle)
            trailingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun buttonBaseStyle(
    size: ButtonSize,
    variant: ButtonVariant,
): Style = Style {
    height(size.height)
    shape(LocalSaionRadius.currentValue.component.full.toRoundedCornerShape())
    clip()
    background(backgroundColor(variant = variant))
    contentPaddingHorizontal(size.horizontalPadding)
}

@Composable
private fun buttonInteractionStyle(colors: SemanticColor): Style = Style {
    pressed {
        animate(tween(100)) {
            foreground(SolidColor(colors.overlay.pressed))
            scale(0.96f)
        }
    }

    disabled {
        foreground(colors.overlay.disabled)
    }
}

private val ButtonSize.height: Dp
    get() = when (this) {
        ButtonSize.XLARGE -> 56.dp
        ButtonSize.LARGE -> 48.dp
        ButtonSize.MEDIUM -> 40.dp
        ButtonSize.SMALL -> 32.dp
    }

private val ButtonSize.horizontalPadding: Dp
    get() = when (this) {
        ButtonSize.XLARGE -> 24.dp
        ButtonSize.LARGE -> 20.dp
        ButtonSize.MEDIUM -> 16.dp
        ButtonSize.SMALL -> 12.dp
    }

private fun StyleScope.backgroundColor(variant: ButtonVariant): Color = with(LocalSaionColors.currentValue) {
    when (variant) {
        ButtonVariant.PRIMARY -> primary.default
        ButtonVariant.SUBTLE -> primary.subtle
        ButtonVariant.NEUTRAL -> fill.default
        ButtonVariant.DANGER -> status.negative.default
    }
}

private val ButtonVariant.contentColor: Color
    @Composable
    get() = when (this) {
        ButtonVariant.PRIMARY,
        ButtonVariant.SUBTLE,
        ButtonVariant.DANGER,
        -> SaionTheme.colors.label.inverse

        ButtonVariant.NEUTRAL -> SaionTheme.colors.label.strong
    }

private val ButtonSize.textStyle: TextStyle
    @Composable
    get() = when (this) {
        ButtonSize.XLARGE -> SaionTheme.typography.title1Subtle
        ButtonSize.LARGE -> SaionTheme.typography.body1
        ButtonSize.MEDIUM -> SaionTheme.typography.label1Subtle
        ButtonSize.SMALL -> SaionTheme.typography.label2
    }

@Preview(showBackground = true, name = "Primary")
@Composable
private fun SaionButtonPrimaryPreview() {
    SaionTheme {
        ButtonVariantPreviewContent(variant = ButtonVariant.PRIMARY)
    }
}

@Preview(showBackground = true, name = "Subtle")
@Composable
private fun SaionButtonSubtlePreview() {
    SaionTheme {
        ButtonVariantPreviewContent(variant = ButtonVariant.SUBTLE)
    }
}

@Preview(showBackground = true, name = "Neutral")
@Composable
private fun SaionButtonNeutralPreview() {
    SaionTheme {
        ButtonVariantPreviewContent(variant = ButtonVariant.NEUTRAL)
    }
}

@Preview(showBackground = true, name = "Danger")
@Composable
private fun SaionButtonDangerPreview() {
    SaionTheme {
        ButtonVariantPreviewContent(variant = ButtonVariant.DANGER)
    }
}

@Composable
private fun ButtonVariantPreviewContent(variant: ButtonVariant) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(2) { time ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ButtonSize.entries.forEach { size ->
                    SaionButton(
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
