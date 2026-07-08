package com.saion.feature.auth.impl.nickname.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface NicknameIntent : UIIntent {
    data object Load : NicknameIntent

    data object BackClicked : NicknameIntent

    data class NicknameChanged(val nickname: String) : NicknameIntent

    data object SubmitClicked : NicknameIntent
}
