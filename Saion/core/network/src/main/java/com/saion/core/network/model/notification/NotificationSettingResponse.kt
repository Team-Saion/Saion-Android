package com.saion.core.network.model.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettingResponse(
    @SerialName("d7Enabled")
    val d7Enabled: Boolean,
    @SerialName("d1Enabled")
    val d1Enabled: Boolean,
    @SerialName("ddayEnabled")
    val ddayEnabled: Boolean,
    @SerialName("familyScheduleCheckEnabled")
    val familyScheduleCheckEnabled: Boolean,
)
