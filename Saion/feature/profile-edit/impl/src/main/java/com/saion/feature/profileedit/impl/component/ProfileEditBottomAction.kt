package com.saion.feature.profileedit.impl.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.saion.core.model.member.NicknameValidationResult
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.button.SaionButtonArea
import com.saion.ds.theme.SaionTheme
import com.saion.feature.profileedit.impl.R

@Composable
internal fun ProfileEditBottomAction(
    validationResult: NicknameValidationResult?,
    isSubmitEnabled: Boolean,
    submitText: String,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SaionBottomCTA(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        upper = {
            Text(
                text = supportingMessage(validationResult),
                style = SaionTheme.typography.title1Subtle,
                color = supportingMessageColor(validationResult),
            )
        },
    ) {
        SaionButtonArea(
            mainButton = { buttonModifier ->
                SaionButton(
                    text = submitText,
                    modifier = buttonModifier,
                    size = ButtonSize.XLARGE,
                    enabled = isSubmitEnabled,
                    onClick = onSubmit,
                )
            },
        )
    }
}

@Composable
private fun supportingMessage(result: NicknameValidationResult?): String = when (result) {
    NicknameValidationResult.Valid,
    null,
    -> androidx.compose.ui.res.stringResource(R.string.profile_edit_validation_guide)

    NicknameValidationResult.Empty -> androidx.compose.ui.res.stringResource(R.string.profile_edit_validation_empty)

    NicknameValidationResult.TooShort -> androidx.compose.ui.res.stringResource(R.string.profile_edit_validation_too_short)

    NicknameValidationResult.TooLong -> androidx.compose.ui.res.stringResource(R.string.profile_edit_validation_too_long)

    NicknameValidationResult.InvalidCharacter ->
        androidx.compose.ui.res.stringResource(R.string.profile_edit_validation_invalid_character)
}

@Composable
private fun supportingMessageColor(result: NicknameValidationResult?): Color = when (result) {
    NicknameValidationResult.Empty,
    NicknameValidationResult.TooShort,
    NicknameValidationResult.TooLong,
    NicknameValidationResult.InvalidCharacter,
    -> SaionTheme.colors.status.negative.default

    NicknameValidationResult.Valid,
    null,
    -> SaionTheme.colors.label.subtle
}
