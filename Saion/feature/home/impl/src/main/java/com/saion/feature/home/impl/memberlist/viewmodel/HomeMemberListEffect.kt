package com.saion.feature.home.impl.memberlist.viewmodel

import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface HomeMemberListEffect : UIEffect {
    data class ShowSnackbar(val message: HomeMemberListSnackbarMessage) : HomeMemberListEffect
}

internal sealed interface HomeMemberListSnackbarMessage {
    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : HomeMemberListSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : HomeMemberListSnackbarMessage
}
