package com.saion.feature.profileedit.impl

import androidx.compose.runtime.Immutable
import com.saion.core.model.member.NicknameValidation
import com.saion.core.ui.viewmodel.UIState

@Immutable
internal data class ProfileEditUiState(
    val initialNickname: String = "",
    val nicknamePlaceholder: String = "",
    val nickname: String = "",
    val validation: NicknameValidation? = null,
    val profileImageUrl: String? = null,
    val selectedProfileImageUri: String? = null,
    val avatarColorHex: String = DEFAULT_AVATAR_COLOR_HEX,
    val isSubmitting: Boolean = false,
) : UIState {
    val hasNicknameChanged: Boolean
        get() = validation?.trimmedNickname != initialNickname.trim()

    val hasProfileImageChanged: Boolean
        get() = selectedProfileImageUri != null

    val hasChanges: Boolean
        get() = hasNicknameChanged || hasProfileImageChanged

    val isSubmitEnabled: Boolean
        get() = validation?.isValid == true && hasChanges && !isSubmitting
}

internal const val DEFAULT_AVATAR_COLOR_HEX: String = "#E5E7EB"
