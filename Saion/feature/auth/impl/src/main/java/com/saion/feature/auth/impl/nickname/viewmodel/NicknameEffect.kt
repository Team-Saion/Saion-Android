package com.saion.feature.auth.impl.nickname.viewmodel

import com.saion.core.ui.viewmodel.UIEffect
import com.saion.feature.auth.impl.ui.AuthSnackbarMessage

internal sealed interface NicknameEffect : UIEffect {
    data object NavigateBack : NicknameEffect

    data object NavigateComplete : NicknameEffect

    data class ShowSnackbar(val message: AuthSnackbarMessage) : NicknameEffect
}
