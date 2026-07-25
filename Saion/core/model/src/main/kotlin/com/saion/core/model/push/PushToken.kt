package com.saion.core.model.push

data class PushToken(
    val id: Long,
    val platform: PushPlatform,
    val active: Boolean,
)
