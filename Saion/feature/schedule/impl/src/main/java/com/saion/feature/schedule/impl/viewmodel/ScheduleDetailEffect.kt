package com.saion.feature.schedule.impl.viewmodel

import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface ScheduleDetailEffect : UIEffect {
    data object Deleted : ScheduleDetailEffect
    data class ShowSnackbar(val message: ScheduleDetailSnackbarMessage) : ScheduleDetailEffect
}

internal sealed interface ScheduleDetailSnackbarMessage {
    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : ScheduleDetailSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : ScheduleDetailSnackbarMessage
}
