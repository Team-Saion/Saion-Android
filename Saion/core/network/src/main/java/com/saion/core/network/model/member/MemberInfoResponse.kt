package com.saion.core.network.model.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberInfoResponse(
    @SerialName("role")
    val role: String,
)
