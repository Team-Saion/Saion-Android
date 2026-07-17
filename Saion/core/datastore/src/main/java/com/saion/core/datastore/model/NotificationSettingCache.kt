package com.saion.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettingCache(
    val hasValue: Boolean,
    val d7Enabled: Boolean,
    val d1Enabled: Boolean,
    val ddayEnabled: Boolean,
    val familyScheduleCheckEnabled: Boolean,
)
