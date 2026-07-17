package com.saion.feature.circlecreate.impl.component

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.button.SaionButtonArea
import com.saion.ds.theme.SaionTheme
import com.saion.feature.circlecreate.impl.R

@Composable
internal fun CircleCreateBottomAction(
    nameLength: Int,
    isNameTooLong: Boolean,
    validationMessage: String?,
    isSubmitEnabled: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    SaionBottomCTA(
        modifier = modifier,
        upper = {
            BottomActionUpperText(
                text = validationMessage ?: "$nameLength/20",
                color = if (validationMessage != null || isNameTooLong) {
                    SaionTheme.colors.status.negative.default
                } else {
                    SaionTheme.colors.label.subtle
                },
            )
        },
    ) {
        SaionButtonArea(
            mainButton = { modifier: Modifier ->
                SaionButton(
                    text = stringResource(R.string.circle_create_submit),
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
