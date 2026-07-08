package com.saion.core.network.model.circle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CircleTransferInitiatorRequest(
    @SerialName("targetMemberId")
    val targetMemberId: String,
)
