package com.saion.feature.auth.impl.nickname.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.model.member.NicknameValidationResult
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.button.SaionButtonArea
import com.saion.ds.theme.SaionTheme
import com.saion.feature.auth.impl.R

@Composable
internal fun NicknameBottomAction(
    validationResult: NicknameValidationResult?,
    isSubmitEnabled: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SaionBottomCTA(
        modifier = modifier
            .navigationBarsPadding()
            .imePadding(),
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
                    text = stringResource(R.string.nickname_submit),
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
    -> stringResource(R.string.nickname_validation_guide)

    NicknameValidationResult.Empty -> stringResource(R.string.nickname_validation_empty)
    NicknameValidationResult.TooShort -> stringResource(R.string.nickname_validation_too_short)
    NicknameValidationResult.TooLong -> stringResource(R.string.nickname_validation_too_long)
    NicknameValidationResult.InvalidCharacter -> stringResource(R.string.nickname_validation_invalid_character)
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

@Preview(showBackground = true)
@Composable
private fun NicknameBottomActionPreview() {
    SaionTheme {
        NicknameBottomAction(
            validationResult = NicknameValidationResult.Valid,
            isSubmitEnabled = true,
            onSubmit = {},
        )
    }
}
