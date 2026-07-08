package com.saion.core.network.model.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssuedInvitationResponse(
    @SerialName("invitationId")
    val invitationId: String,
    @SerialName("token")
    val token: String,
    @SerialName("expiresAt")
    val expiresAt: String,
)
