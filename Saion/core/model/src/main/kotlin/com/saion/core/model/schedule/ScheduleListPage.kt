package com.saion.core.model.schedule

data class ScheduleListPage(
    val schedules: List<ScheduleSummary>,
    val nextCursor: String?,
    val hasNext: Boolean,
)
