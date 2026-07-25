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
        val isFamilyNotificationDialogVisible: Boolean = false,
        val isRequestingFamilyNotification: Boolean = false,
        val mainSchedule: ScheduleSummary?,
        val schedules: ImmutableList<ScheduleSummary>,
    ) : HomeState {
        val heroSchedule: ScheduleSummary? = mainSchedule

        val sectionSchedules: ImmutableList<ScheduleSummary>
            get() = schedules.take(3).toImmutableList()
    }

    val shouldShowHero: Boolean
        get() = when (this) {
            Loading -> false
            None -> true
            is Content -> heroSchedule != null || members.none { member -> member.isMe.not() }
        }

    val shouldShowAddSchedule: Boolean
        get() = when(this) {
            is Content -> this.sectionSchedules.size < 3
            None -> true
            Loading -> false
        }
}

internal fun HomeOverview.toUiState(
    isInviting: Boolean = false,
    isFamilyNotificationDialogVisible: Boolean = false,
    isRequestingFamilyNotification: Boolean = false,
): HomeState.Content = HomeState.Content(
    circle = circle,
    members = members.toImmutableList(),
    canInvite = canInvite,
    isInviting = isInviting,
    isFamilyNotificationDialogVisible = isFamilyNotificationDialogVisible,
    isRequestingFamilyNotification = isRequestingFamilyNotification,
    mainSchedule = mainSchedule,
    schedules = schedules.toImmutableList(),
)
