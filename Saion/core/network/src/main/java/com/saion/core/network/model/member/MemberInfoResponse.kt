package com.saion.core.network.model.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberInfoResponse(
    @SerialName("id")
    val id: String,
    @SerialName("email")
    val email: String,
    @SerialName("name")
    val name: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("role")
    val role: String,
    @SerialName("profileImageKey")
    val profileImageKey: String? = null,
    @SerialName("status")
    val status: String,
    @SerialName("createdAt")
    val createdAt: String,
)
