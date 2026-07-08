package com.saion.core.model.home

data class CircleMember(
    val memberId: String,
    val nickname: String,
    val avatarColor: String,
    val isMe: Boolean,
    val role: String,
)
