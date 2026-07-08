package com.saion.core.model.schedule

data class UpdateScheduleCommand(
    val title: ScheduleUpdateValue<String> = ScheduleUpdateValue.Unchanged,
    val startDate: ScheduleUpdateValue<String> = ScheduleUpdateValue.Unchanged,
    val endDate: ScheduleUpdateValue<String> = ScheduleUpdateValue.Unchanged,
    val startTime: ScheduleUpdateValue<String> = ScheduleUpdateValue.Unchanged,
    val endTime: ScheduleUpdateValue<String> = ScheduleUpdateValue.Unchanged,
    val needConfirm: Boolean,
    val memo: ScheduleUpdateValue<String> = ScheduleUpdateValue.Unchanged,
)
