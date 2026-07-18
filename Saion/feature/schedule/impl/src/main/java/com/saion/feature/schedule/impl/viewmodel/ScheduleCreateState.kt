package com.saion.feature.schedule.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.ui.viewmodel.UIState
import java.time.LocalDate
import java.time.LocalTime

@Immutable
internal data class ScheduleCreateState(
    val title: String = "",
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val needConfirm: Boolean = false,
    val memo: String = "",
    val isSubmitting: Boolean = false,
) : UIState {
    val isSubmitEnabled: Boolean
        get() = title.isNotBlank() && endDate.atTime(endTime) >= startDate.atTime(startTime) && isSubmitting.not()
}
