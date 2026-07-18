package com.saion.feature.schedule.impl.viewmodel

import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface ScheduleDetailIntent : UIIntent {
    data class Load(val scheduleId: String) : ScheduleDetailIntent
    data class ConfirmationClicked(val type: ConfirmationType) : ScheduleDetailIntent
    data object DeleteClicked : ScheduleDetailIntent
    data object DeleteConfirmed : ScheduleDetailIntent
    data object DeleteDismissed : ScheduleDetailIntent
}
