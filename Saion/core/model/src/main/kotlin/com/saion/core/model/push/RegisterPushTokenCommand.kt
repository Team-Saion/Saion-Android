package com.saion.core.model.push

data class RegisterPushTokenCommand(
    val token: String,
    val platform: PushPlatform,
    val osNotificationPermissionGranted: Boolean,
    val appVersion: String?,
)
