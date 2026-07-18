package com.saion.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class MemberProfileCache(
    val hasValue: Boolean,
    val nickname: String,
    val profileImageUrl: String,
    val avatarColorHex: String,
    val role: String,
    val status: String,
)
