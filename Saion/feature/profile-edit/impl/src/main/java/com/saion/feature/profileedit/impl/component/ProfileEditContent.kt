package com.saion.feature.profileedit.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.saion.core.model.member.NicknameValidationResult
import com.saion.core.ui.component.SaionProfile
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.component.input.SaionTextArea
import com.saion.ds.component.input.SaionTextAreaVariant
import com.saion.ds.theme.SaionTheme
import com.saion.feature.profileedit.impl.R

@Composable
internal fun ProfileEditContent(
    title: String,
    nickname: String,
    placeholder: String,
    validationResult: NicknameValidationResult?,
    imageUrl: String?,
    avatarColorHex: String,
    focusRequester: FocusRequester,
    onProfileImageClick: () -> Unit,
    onNicknameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        SaionProfile(
            nickname = nickname,
            textStyle = SaionTheme.typography.display2,
            imageUrl = imageUrl,
            avatarColorHex = avatarColorHex,
            isShowEdit = true,
            contentDescription = stringResource(R.string.profile_edit_profile_image_description),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
                .noRippleClickable(onClick = onProfileImageClick),
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = title,
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
