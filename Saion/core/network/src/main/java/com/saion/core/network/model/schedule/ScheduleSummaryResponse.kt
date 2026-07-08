package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleSummaryResponse(
    @SerialName("scheduleId")
    val scheduleId: String,
    @SerialName("title")
    val title: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String,
    @SerialName("startTime")
    val startTime: String? = null,
    @SerialName("endTime")
    val endTime: String? = null,
    @SerialName("isAllDay")
    val isAllDay: Boolean,
    @SerialName("needConfirm")
    val needConfirm: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("progressRate")
    val progressRate: Int,
    @SerialName("dDay")
    val dDay: Int? = null,
    @SerialName("dday")
    val dday: Int? = null,
)
