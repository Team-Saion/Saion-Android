package com.saion.core.network.model.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssueInvitationRequest(
    @SerialName("type")
    val type: String,
    @SerialName("targetId")
    val targetId: String,
    @SerialName("inviteToName")
    val inviteToName: String? = null,
    @SerialName("message")
    val message: String? = null,
)
