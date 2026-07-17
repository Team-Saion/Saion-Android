package com.saion.feature.schedule.impl.viewmodel

import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface ScheduleEffect : UIEffect {
    data class ShowSnackbar(val message: ScheduleSnackbarMessage) : ScheduleEffect
}

internal sealed interface ScheduleSnackbarMessage {
    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : ScheduleSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : ScheduleSnackbarMessage
}
