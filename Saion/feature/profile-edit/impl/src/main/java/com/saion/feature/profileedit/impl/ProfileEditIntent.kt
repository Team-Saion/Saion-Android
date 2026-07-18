package com.saion.feature.profileedit.impl

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface ProfileEditIntent : UIIntent {
    data object Load : ProfileEditIntent

    data object BackClicked : ProfileEditIntent

    data class ProfileImageSelected(val imageUri: String?) : ProfileEditIntent

    data class NicknameChanged(val nickname: String) : ProfileEditIntent

    data object SubmitClicked : ProfileEditIntent
}
