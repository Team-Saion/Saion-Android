package com.saion.core.network.model.home

import com.saion.core.network.model.circle.CircleSummaryResponse
import com.saion.core.network.model.schedule.ScheduleSummaryResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CircleHomeResponse(
    @SerialName("circle")
    val circle: CircleSummaryResponse,
    @SerialName("members")
    val members: List<CircleMemberResponse>,
    @SerialName("canInvite")
    val canInvite: Boolean,
    @SerialName("mainSchedule")
    val mainSchedule: ScheduleSummaryResponse? = null,
    @SerialName("schedules")
    val schedules: List<ScheduleSummaryResponse>,
    @SerialName("totalScheduleCount")
    val totalScheduleCount: Long,
)
