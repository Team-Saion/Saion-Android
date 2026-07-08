package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationCountResponse(
    @SerialName("type")
    val type: String,
    @SerialName("count")
    val count: Int,
)
