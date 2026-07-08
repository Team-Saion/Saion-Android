package com.saion.core.network.model.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitationDetailResponse(
    @SerialName("invitationId")
    val invitationId: String,
    @SerialName("circleName")
    val circleName: String,
    @SerialName("inviter")
    val inviter: InviterResponse,
    @SerialName("expiresAt")
    val expiresAt: String,
)
