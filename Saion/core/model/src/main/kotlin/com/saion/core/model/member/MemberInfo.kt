package com.saion.core.model.member

data class MemberInfo(
    val nickname: String,
    val profileImageUrl: String?,
    val avatarColorHex: String,
    val role: MemberRole,
    val status: MemberStatus,
)
