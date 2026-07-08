package com.saion.core.model.schedule

data class CreateScheduleCommand(
    val title: String,
    val startDate: String,
    val endDate: String,
    val startTime: String? = null,
    val endTime: String? = null,
    val needConfirm: Boolean,
    val memo: String? = null,
)
