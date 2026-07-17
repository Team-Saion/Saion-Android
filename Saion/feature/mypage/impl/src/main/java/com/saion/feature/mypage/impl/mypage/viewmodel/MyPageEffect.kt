package com.saion.feature.mypage.impl.mypage.viewmodel

import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface MyPageEffect : UIEffect {
    data class ShowSnackbar(val message: MyPageSnackbarMessage) : MyPageEffect

    data object LogoutCompleted : MyPageEffect
}

internal sealed interface MyPageSnackbarMessage {
    data class Text(
        val value: String,
        val defaultMessageResId: Int,
    ) : MyPageSnackbarMessage

    data class Error(
        val error: AppError,
        val defaultMessageResId: Int,
    ) : MyPageSnackbarMessage
}
