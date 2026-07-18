package com.saion.feature.profileedit.impl

import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface ProfileEditEffect : UIEffect {
    data object NavigateBack : ProfileEditEffect

    data object NavigateComplete : ProfileEditEffect

    data class ShowSnackbar(val message: ProfileEditSnackbarMessage) : ProfileEditEffect
}
