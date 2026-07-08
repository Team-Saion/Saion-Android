package com.saion.feature.auth.impl.nickname.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.member.NicknameValidation
import com.saion.core.ui.viewmodel.UIState

@Immutable
internal data class NicknameUiState(
    val nickNamePlaceholder: String = "",
    val nickname: String = "",
    val validation: NicknameValidation? = null,
    val socialProfileImageUrl: String? = null,
    val avatarColorHex: String = "#E5E7EB",
    val isSubmitting: Boolean = false,
) : UIState {
    val isSubmitEnabled: Boolean
        get() = validation?.isValid == true && !isSubmitting
}
