package com.saion.core.model.schedule

data class ScheduleSummary(
    val scheduleId: String,
    val title: String,
    val startDate: String,
    val endDate: String,
    val startTime: String?,
    val endTime: String?,
    val isAllDay: Boolean,
    val needConfirm: Boolean,
    val status: ScheduleStatus,
    val progressRate: Int,
    val dday: Int?,
)
