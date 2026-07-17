package com.saion.feature.schedule.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.schedule.ScheduleListPage
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.ui.viewmodel.UIState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal sealed interface ScheduleState : UIState {
    data object Loading : ScheduleState

    data object Empty : ScheduleState

    data object Error : ScheduleState

    data class Content(
        val schedules: ImmutableList<ScheduleSummary>,
        val isRefreshing: Boolean,
        val isAppending: Boolean,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : ScheduleState
}

internal fun ScheduleListPage.toContentState(isRefreshing: Boolean = false): ScheduleState = if (schedules.isEmpty()) {
    ScheduleState.Empty
} else {
    ScheduleState.Content(
        schedules = schedules.toImmutableList(),
        isRefreshing = isRefreshing,
        isAppending = false,
        nextCursor = nextCursor,
        hasNext = hasNext,
    )
}
