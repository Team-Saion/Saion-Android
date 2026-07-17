package com.saion.feature.invitation.impl.viewmodel

import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface InvitationAcceptEffect : UIEffect {
    data object Close : InvitationAcceptEffect

    data class ShowSnackbar(val message: InvitationAcceptSnackbarMessage) : InvitationAcceptEffect
}

internal sealed interface InvitationAcceptSnackbarMessage {
    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : InvitationAcceptSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : InvitationAcceptSnackbarMessage
}
