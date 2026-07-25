package com.saion.core.network.model.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class NotificationSettingResponse(
    @SerialName("d7Enabled")
    val d7Enabled: Boolean,
    @SerialName("d1Enabled")
    val d1Enabled: Boolean,
    @SerialName("dDayEnabled")
    @JsonNames("ddayEnabled")
    val dDayEnabled: Boolean,
    @SerialName("familyScheduleCheckEnabled")
    val familyScheduleCheckEnabled: Boolean,
)
