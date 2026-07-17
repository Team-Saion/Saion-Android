package com.saion.feature.invitation.api.key

import com.saion.core.navigation.key.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class InvitationAcceptNavKey(val token: String) : AppNavKey
