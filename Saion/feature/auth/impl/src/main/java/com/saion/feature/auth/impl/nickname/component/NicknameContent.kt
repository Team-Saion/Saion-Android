package com.saion.feature.auth.impl.nickname.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.model.member.NicknameValidationResult
import com.saion.ds.component.input.SaionTextArea
import com.saion.ds.component.input.SaionTextAreaVariant
import com.saion.ds.theme.SaionTheme

@Composable
internal fun NicknameContent(
    nickname: String,
    placeholder: String,
    validationResult: NicknameValidationResult?,
    imageUrl: String?,
    avatarColorHex: String,
    focusRequester: FocusRequester,
    onNicknameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        NicknameProfileImage(
            imageUrl = imageUrl,
            avatarColorHex = avatarColorHex,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(40.dp))

        androidx.compose.material3.Text(
            text = "어떻게 불러드릴까요?",
            style = SaionTheme.typography.title2,
            color = SaionTheme.colors.label.default,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(12.dp))

        SaionTextArea(
            value = nickname,
            onValueChange = onNicknameChange,
            variant = SaionTextAreaVariant.NONE,
            placeholder = placeholder,
            isError = validationResult in setOf(
                NicknameValidationResult.TooShort,
                NicknameValidationResult.TooLong,
                NicknameValidationResult.InvalidCharacter,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            minLines = 1,
            maxLines = 1,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NicknameContentPreview() {
    SaionTheme {
        NicknameContent(
            nickname = "사이온",
            placeholder = "닉네임을 입력해주세요",
            validationResult = NicknameValidationResult.Valid,
            imageUrl = null,
            avatarColorHex = "#FFD35C",
            focusRequester = FocusRequester(),
            onNicknameChange = {},
        )
    }
}
