package com.saion.feature.schedule.impl.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface ScheduleIntent : UIIntent {
    data object RefreshRequested : ScheduleIntent

    data object LoadNextPageRequested : ScheduleIntent
}
