package com.saion.ds.component.button

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.LocalSaionColors
import com.saion.ds.theme.LocalSaionRadius
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

enum class IconButtonSize {
    LARGE,
    MEDIUM,
    SMALL,
}

@Composable
fun SaionIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = SaionTheme.colors.label.strong,
    size: IconButtonSize = IconButtonSize.MEDIUM,
    enabled: Boolean = true,
    contentDescription: String? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
) {
    val styleState = rememberUpdatedStyleState(interactionSource) { state ->
        state.isEnabled = enabled
    }

    Box(
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
                buttonBaseStyle(size = size),
                buttonInteractionStyle(),
            ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides if (enabled) tint else SaionTheme.colors.label.disabled,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(size = size.iconSize),
            )
        }
    }
}

@Composable
private fun buttonBaseStyle(size: IconButtonSize): Style = Style {
    shape(shape(size = size))
    clip()
    contentPadding(size.contentPadding)
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

private val IconButtonSize.iconSize: Dp
    @Composable
    get() = when (this) {
        IconButtonSize.LARGE -> 24.dp
        IconButtonSize.MEDIUM -> 20.dp
        IconButtonSize.SMALL -> 16.dp
    }

private fun StyleScope.shape(size: IconButtonSize): RoundedCornerShape = with(LocalSaionRadius.currentValue) {
    when (size) {
        IconButtonSize.LARGE -> component.large
        IconButtonSize.MEDIUM -> component.medium
        IconButtonSize.SMALL -> component.small
    }.toRoundedCornerShape()
}

private val IconButtonSize.contentPadding: Dp
    get() = when (this) {
        IconButtonSize.LARGE -> 10.dp
        IconButtonSize.MEDIUM -> 8.dp
        IconButtonSize.SMALL -> 6.dp
    }

@Preview(showBackground = true, name = "IconButton")
@Composable
private fun SaionIconButtonPreview() {
    SaionTheme {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(2) { time ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButtonSize.entries.forEach { size ->
                        SaionIconButton(
                            icon = SaionIcons.Selection,
                            size = size,
                            enabled = time % 2 == 0,
                            onClick = {},
                        )
                    }
                }
            }
        }
    }
}
