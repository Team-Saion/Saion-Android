package com.saion.core.model.home

import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.schedule.ScheduleSummary

data class HomeOverview(
    val circle: CircleSummary,
    val members: List<CircleMember>,
    val canInvite: Boolean,
    val mainSchedule: ScheduleSummary?,
    val schedules: List<ScheduleSummary>,
    val totalScheduleCount: Long,
)
