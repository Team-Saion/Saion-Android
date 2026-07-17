package com.saion.feature.home.impl.home.viewmodel

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
        val isInviting: Boolean,
        val schedules: ImmutableList<ScheduleSummary>,
        val totalScheduleCount: Long,
    ) : HomeState {
        val heroSchedule: ScheduleSummary?
            get() = schedules.firstOrNull()

        val sectionSchedules: ImmutableList<ScheduleSummary>
            get() = schedules.drop(1).take(3).toImmutableList()
    }

    val shouldShowHero: Boolean
        get() = when (this) {
            Loading -> false
            None -> true
            is Content -> heroSchedule != null || members.none { member -> member.isMe.not() }
        }
}

internal fun HomeOverview.toUiState(isInviting: Boolean = false): HomeState.Content = HomeState.Content(
    circle = circle,
    members = members.toImmutableList(),
    canInvite = canInvite,
    isInviting = isInviting,
    schedules = schedules.toImmutableList(),
    totalScheduleCount = totalScheduleCount,
)
