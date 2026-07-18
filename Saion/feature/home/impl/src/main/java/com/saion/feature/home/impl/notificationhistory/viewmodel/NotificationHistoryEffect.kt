package com.saion.feature.home.impl.notificationhistory.viewmodel

import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface NotificationHistoryEffect : UIEffect {
    data class ShowSnackbar(val message: NotificationHistorySnackbarMessage) : NotificationHistoryEffect

    data object NavigateToHome : NotificationHistoryEffect

    data object NavigateToSchedule : NotificationHistoryEffect

    data class NavigateToScheduleDetail(val scheduleId: String) : NotificationHistoryEffect
}

internal sealed interface NotificationHistorySnackbarMessage {
    data class Text(
        val value: String,
        val defaultMessageResId: Int,
    ) : NotificationHistorySnackbarMessage

    data class Error(
        val error: AppError,
        val defaultMessageResId: Int,
    ) : NotificationHistorySnackbarMessage
}
