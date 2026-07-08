package com.saion.feature.auth.impl.nickname.viewmodel

import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface NicknameEffect : UIEffect {
    data object NavigateBack : NicknameEffect

    data object NavigateComplete : NicknameEffect

    data class ShowSnackbar(val message: String) : NicknameEffect
}
