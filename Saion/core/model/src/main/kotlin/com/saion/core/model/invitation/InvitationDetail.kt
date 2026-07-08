package com.saion.core.model.invitation

data class InvitationDetail(
    val invitationId: String,
    val circleName: String,
    val inviter: InvitationIssuer,
    val expiresAt: String,
)
