package com.saion.feature.schedule.impl.viewmodel

import com.saion.core.ui.viewmodel.UIIntent
import java.time.LocalDate
import java.time.LocalTime

internal sealed interface ScheduleCreateIntent : UIIntent {
    data class TitleChanged(val value: String) : ScheduleCreateIntent
    data class StartDateChanged(val value: LocalDate) : ScheduleCreateIntent
    data class EndDateChanged(val value: LocalDate) : ScheduleCreateIntent
    data class StartTimeChanged(val value: LocalTime) : ScheduleCreateIntent
    data class EndTimeChanged(val value: LocalTime) : ScheduleCreateIntent
    data class NeedConfirmChanged(val value: Boolean) : ScheduleCreateIntent
    data class MemoChanged(val value: String) : ScheduleCreateIntent
    data object SubmitClicked : ScheduleCreateIntent
}
