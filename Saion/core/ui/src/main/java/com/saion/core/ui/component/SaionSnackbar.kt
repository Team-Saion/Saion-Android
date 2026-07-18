package com.saion.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.SnackbarResult.Dismissed
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionButton
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

enum class SaionSnackbarVariant {
    Positive,
    Cautionary,
    Negative,
}

data class SaionSnackbarVisuals(
    override val message: String,
    val variant: SaionSnackbarVariant? = null,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals

suspend fun SnackbarHostState.showSaionSnackbar(
    message: String,
    variant: SaionSnackbarVariant? = null,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onDismiss: (() -> Unit)? = null,
    onActionPerform: (() -> Unit)? = null,
) {
    when (
        showSnackbar(
            visuals = SaionSnackbarVisuals(
                message = message,
                variant = variant,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = duration,
            ),
        )
    ) {
        ActionPerformed -> onActionPerform?.invoke()
        Dismissed -> onDismiss?.invoke()
    }
}

@Composable
fun SaionSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.navigationBarsPadding(),
        snackbar = { data ->
            SaionSnackbar(
                message = data.visuals.message,
                variant = (data.visuals as? SaionSnackbarVisuals)?.variant,
                actionLabel = data.visuals.actionLabel,
                onActionClick = data::performAction,
            )
        },
    )
}

@Composable
fun SaionSnackbar(
    message: String,
    variant: SaionSnackbarVariant?,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val hasAction = actionLabel != null && onActionClick != null
    val contentPadding =
        if (hasAction) {
            PaddingValues(horizontal = 16.dp, vertical = 14.dp)
        } else {
            PaddingValues(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        }

    Surface(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        shape = snackbarShape(hasAction = hasAction),
        color = SnackbarContainerColor,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .then(
                    if (hasAction) Modifier.fillMaxWidth() else Modifier
                )
                .padding(contentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                variant?.let { iconVariant ->
                    StatusBadge(variant = iconVariant)
                }

                Text(
                    text = message,
                    style = SaionTheme.typography.title3,
                    color = SaionTheme.colors.label.inverse,
                )
            }

            if (hasAction) {
                Spacer(modifier = Modifier.width(12.dp))
                SaionButton(
                    text = actionLabel,
                    onClick = onActionClick,
                    variant = ButtonVariant.NEUTRAL,
                    size = ButtonSize.SMALL
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(variant: SaionSnackbarVariant) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(SaionTheme.colors.label.inverse)
        )
        Icon(
            imageVector = variant.icon(),
            contentDescription = null,
            tint = variant.badgeColor(),
        )
    }
}

@Composable
private fun SaionSnackbarVariant.badgeColor(): Color = when (this) {
    SaionSnackbarVariant.Positive -> SaionTheme.colors.status.positive.default
    SaionSnackbarVariant.Cautionary -> SaionTheme.colors.status.cautionary.default
    SaionSnackbarVariant.Negative -> SaionTheme.colors.status.negative.default
}

private fun SaionSnackbarVariant.icon(): ImageVector = when (this) {
    SaionSnackbarVariant.Positive -> SaionIcons.CheckFilled
    SaionSnackbarVariant.Cautionary -> SaionIcons.Warning
    SaionSnackbarVariant.Negative -> SaionIcons.Warning
}

@Composable
private fun snackbarShape(hasAction: Boolean) = when (hasAction) {
    true -> SaionTheme.radius.component.xxLarge.toRoundedCornerShape()
    false -> SaionTheme.radius.component.full.toRoundedCornerShape()
}

private val SnackbarContainerColor: Color = Color(0xFF6C757F)

@Preview(showBackground = true, name = "Variants")
@Composable
private fun SaionSnackbarVariantPreview() {
    SaionTheme {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SaionSnackbar(message = "Toast", variant = SaionSnackbarVariant.Positive)
            SaionSnackbar(message = "Toast", variant = SaionSnackbarVariant.Cautionary)
            SaionSnackbar(message = "Toast", variant = SaionSnackbarVariant.Negative)
            SaionSnackbar(message = "Toast", variant = null)
            SaionSnackbar(
                message = "Toast",
                variant = null,
                actionLabel = "Button",
                onActionClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Without Icon")
@Composable
private fun SaionSnackbarWithoutIconPreview() {
    SaionTheme {
        SaionSnackbar(message = "Toast", variant = null)
    }
}

@Preview(showBackground = true, name = "With Action")
@Composable
private fun SaionSnackbarWithActionPreview() {
    SaionTheme {
        SaionSnackbar(
            message = "SnackBar",
            variant = SaionSnackbarVariant.Cautionary,
            actionLabel = "Button",
            onActionClick = {},
        )
    }
}
