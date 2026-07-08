package com.saion.core.network.model.circle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CircleSummaryResponse(
    @SerialName("circleId")
    val circleId: String,
    @SerialName("name")
    val name: String,
    @SerialName("ownerId")
    val ownerId: String,
)
