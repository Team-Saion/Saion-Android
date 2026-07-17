package com.saion.feature.circlecreate.impl.component

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
    validationMessage: String?,
    isSubmitEnabled: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    SaionBottomCTA(
        modifier = modifier,
        upper = validationMessage?.let { message ->
            {
                Text(
                    text = message,
                    style = SaionTheme.typography.label1Subtle,
                    color = SaionTheme.colors.status.negative.default,
                )
            }
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
