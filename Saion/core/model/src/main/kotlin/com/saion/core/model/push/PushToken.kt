package com.saion.core.model.push

data class PushToken(
    val id: Long,
    val platform: PushPlatform,
    val osNotificationPermissionGranted: Boolean,
    val appVersion: String?,
    val active: Boolean,
)
