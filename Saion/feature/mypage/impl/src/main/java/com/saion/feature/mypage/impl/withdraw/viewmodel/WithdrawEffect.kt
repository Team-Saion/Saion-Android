package com.saion.feature.mypage.impl.withdraw.viewmodel

import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface WithdrawEffect : UIEffect {
    data class ShowSnackbar(val message: WithdrawSnackbarMessage) : WithdrawEffect

    data object WithdrawCompleted : WithdrawEffect
}

internal sealed interface WithdrawSnackbarMessage {
    data class Text(
        val value: String,
        val defaultMessageResId: Int,
    ) : WithdrawSnackbarMessage

    data class Error(
        val error: AppError,
        val defaultMessageResId: Int,
    ) : WithdrawSnackbarMessage
}
