package com.saion.core.network.model.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssueInvitationRequest(
    @SerialName("targetId")
    val targetId: String,
)
