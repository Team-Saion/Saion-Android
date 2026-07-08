package com.saion.core.model.invitation

data class IssuedInvitation(
    val invitationId: String,
    val token: String,
    val expiresAt: String,
)
