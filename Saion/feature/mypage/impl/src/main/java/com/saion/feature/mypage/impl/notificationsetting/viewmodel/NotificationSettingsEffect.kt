package com.saion.feature.mypage.impl.notificationsetting.viewmodel

import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface NotificationSettingsEffect : UIEffect {
    data class ShowSnackbar(val message: NotificationSettingsSnackbarMessage) : NotificationSettingsEffect
}

internal sealed interface NotificationSettingsSnackbarMessage {
    data class Text(
        val value: String,
        val defaultMessageResId: Int,
    ) : NotificationSettingsSnackbarMessage

    data class Error(
        val error: AppError,
        val defaultMessageResId: Int,
    ) : NotificationSettingsSnackbarMessage
}
