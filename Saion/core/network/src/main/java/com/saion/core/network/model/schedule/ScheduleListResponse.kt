package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleListResponse(
    @SerialName("schedules")
    val schedules: List<ScheduleSummaryResponse>,
    @SerialName("nextCursor")
    val nextCursor: String? = null,
    @SerialName("hasNext")
    val hasNext: Boolean,
)
