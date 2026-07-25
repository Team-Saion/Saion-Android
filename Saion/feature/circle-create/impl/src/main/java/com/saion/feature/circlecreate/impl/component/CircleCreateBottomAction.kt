package com.saion.feature.circlecreate.impl.component

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.button.SaionButtonArea
import com.saion.ds.theme.SaionTheme

@Composable
internal fun CircleCreateBottomAction(
    supportingMessage: String?,
    supportingMessageColor: Color?,
    submitText: String,
    isSubmitEnabled: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SaionBottomCTA(
        modifier = modifier,
        upper = supportingMessage?.let { message ->
            {
                BottomActionUpperText(
                    text = message,
                    color = supportingMessageColor ?: SaionTheme.colors.label.subtle,
                )
            }
        },
    ) {
        SaionButtonArea(
            mainButton = { modifier: Modifier ->
                SaionButton(
                    text = submitText,
                    modifier = modifier,
                    size = ButtonSize.XLARGE,
                    enabled = isSubmitEnabled && !isSubmitting,
                    onClick = onSubmit,
                )
            },
        )
    }
}

@Composable
private fun BottomActionUpperText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = SaionTheme.typography.label1Subtle,
        color = color,
    )
}
