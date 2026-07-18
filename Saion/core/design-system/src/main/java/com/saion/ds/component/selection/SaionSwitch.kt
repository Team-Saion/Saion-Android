package com.saion.ds.component.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

private val SwitchWidth = 64.dp
private val SwitchHeight = 28.dp
private val SwitchPadding = 2.dp
private val SwitchThumbSize = 40.dp
private val SwitchBorderWidth = 1.dp
private const val SWITCH_ANIMATION_DURATION = 180

@Composable
fun SaionSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val pressed by interactionSource.collectIsPressedAsState()
    val trackColor by animateColorAsState(
        targetValue = switchTrackColor(
            checked = checked,
            enabled = enabled,
        ),
        animationSpec = tween(durationMillis = SWITCH_ANIMATION_DURATION),
        label = "saion_switch_track_color",
    )
    val borderColor by animateColorAsState(
        targetValue = switchBorderColor(
            checked = checked,
            enabled = enabled,
        ),
        animationSpec = tween(durationMillis = SWITCH_ANIMATION_DURATION),
        label = "saion_switch_border_color",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) {
            SwitchWidth - SwitchThumbSize - (SwitchPadding * 2)
        } else {
            0.dp
        },
        animationSpec = tween(durationMillis = SWITCH_ANIMATION_DURATION),
        label = "saion_switch_thumb_offset",
    )
    val thumbScale by animateFloatAsState(
        targetValue = if (pressed && enabled) 1.04f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "saion_switch_thumb_scale",
    )
    val thumbColor by animateColorAsState(
        targetValue = switchThumbColor(enabled = enabled),
        animationSpec = tween(durationMillis = SWITCH_ANIMATION_DURATION),
        label = "saion_switch_thumb_color",
    )
    val switchShape = SaionTheme.radius.component.full.toRoundedCornerShape()

    Box(
        modifier = modifier
            .requiredSize(width = SwitchWidth, height = SwitchHeight)
            .clip(switchShape)
            .background(color = trackColor, shape = switchShape)
            .border(
                width = SwitchBorderWidth,
                color = borderColor,
                shape = switchShape,
            )
            .toggleable(
                value = checked,
                enabled = enabled && onCheckedChange != null,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
                onValueChange = { onCheckedChange?.invoke(it) },
            )
            .padding(SwitchPadding),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .shadow(
                        elevation = if (enabled) 3.dp else 1.dp,
                        shape = CircleShape,
                        clip = false,
                    )
                    .size(SwitchThumbSize)
                    .clip(CircleShape)
                    .background(thumbColor)
                    .align(Alignment.CenterStart)
                    .graphicsLayer(
                        scaleX = thumbScale,
                        scaleY = thumbScale,
                    ),
            )
        }
    }
}

@Composable
private fun switchTrackColor(
    checked: Boolean,
    enabled: Boolean,
): Color = when {
    !enabled && checked -> SaionTheme.colors.status.positive.default.copy(alpha = 0.38f)
    !enabled && !checked -> SaionTheme.colors.fill.disabled.copy(alpha = 0.72f)
    checked -> SaionTheme.colors.status.positive.default
    else -> SaionTheme.colors.fill.disabled
}

@Composable
private fun switchBorderColor(
    checked: Boolean,
    enabled: Boolean,
): Color = when {
    checked -> Color.Transparent
    !enabled -> SaionTheme.colors.line.default.copy(alpha = 0.6f)
    else -> SaionTheme.colors.line.default
}

@Composable
private fun switchThumbColor(enabled: Boolean): Color = if (enabled) {
    SaionTheme.colors.background.default
} else {
    SaionTheme.colors.background.default.copy(alpha = 0.92f)
}

@Preview(showBackground = true, name = "States")
@Composable
private fun SaionSwitchPreview() {
    SaionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SwitchPreviewRow(label = "On", checked = true, enabled = true)
            SwitchPreviewRow(label = "Off", checked = false, enabled = true)
            SwitchPreviewRow(label = "On Disabled", checked = true, enabled = false)
            SwitchPreviewRow(label = "Off Disabled", checked = false, enabled = false)
        }
    }
}

@Composable
private fun SwitchPreviewRow(
    label: String,
    checked: Boolean,
    enabled: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            style = SaionTheme.typography.body1,
            color = SaionTheme.colors.label.default,
        )
        SaionSwitch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = {},
        )
    }
}
