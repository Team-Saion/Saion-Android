package com.saion.feature.home.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.ui.viewmodel.UIState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal sealed interface HomeState : UIState {

    data object Loading : HomeState

    data object None : HomeState

    data class Content(
        val circle: CircleSummary,
        val members: ImmutableList<CircleMember>,
        val canInvite: Boolean,
        val mainSchedule: ScheduleSummary?,
        val schedules: ImmutableList<ScheduleSummary>,
        val totalScheduleCount: Long,
    ) : HomeState
}

internal fun HomeOverview.toUiState(): HomeState.Content = HomeState.Content(
    circle = circle,
    members = members.toImmutableList(),
    canInvite = canInvite,
    mainSchedule = mainSchedule,
    schedules = schedules.toImmutableList(),
    totalScheduleCount = totalScheduleCount,
)
