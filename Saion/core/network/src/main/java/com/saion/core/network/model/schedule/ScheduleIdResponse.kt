package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleIdResponse(
    @SerialName("scheduleId")
    val scheduleId: String,
)
