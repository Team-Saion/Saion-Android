package com.saion.core.network.model.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcceptInvitationResponse(
    @SerialName("circleId")
    val circleId: String,
)
