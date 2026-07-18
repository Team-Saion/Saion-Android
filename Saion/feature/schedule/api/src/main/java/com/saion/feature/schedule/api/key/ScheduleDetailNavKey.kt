package com.saion.feature.schedule.api.key

import com.saion.feature.main.api.key.MainTabNavKey
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDetailNavKey(
    val scheduleId: String,
) : MainTabNavKey
