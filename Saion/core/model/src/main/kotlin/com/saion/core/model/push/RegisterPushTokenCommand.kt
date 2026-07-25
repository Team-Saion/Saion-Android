package com.saion.core.model.push

data class RegisterPushTokenCommand(
    val installationId: String,
    val token: String,
    val platform: PushPlatform,
)
