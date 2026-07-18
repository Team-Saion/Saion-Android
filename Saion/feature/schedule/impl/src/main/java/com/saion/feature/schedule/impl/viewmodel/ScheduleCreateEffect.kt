package com.saion.feature.schedule.impl.viewmodel

import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface ScheduleCreateEffect : UIEffect {
    data class NavigateToDetail(val scheduleId: String) : ScheduleCreateEffect
    data class ShowSnackbar(val message: ScheduleCreateSnackbarMessage) : ScheduleCreateEffect
}

internal sealed interface ScheduleCreateSnackbarMessage {
    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : ScheduleCreateSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : ScheduleCreateSnackbarMessage
}
