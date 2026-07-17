package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDetailResponse(
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
    @SerialName("urgencyLevel")
    val urgencyLevel: String,
    @SerialName("progressRate")
    val progressRate: Int,
    @SerialName("memo")
    val memo: String? = null,
    @SerialName("confirmations")
    val confirmations: List<ConfirmationCountResponse>,
    @SerialName("myConfirmation")
    val myConfirmation: MyConfirmationResponse? = null,
    @SerialName("createdBy")
    val createdBy: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("dDay")
    val dDay: Int? = null,
    @SerialName("dday")
    val dday: Int? = null,
)
