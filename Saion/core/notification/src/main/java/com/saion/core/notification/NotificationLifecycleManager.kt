package com.saion.core.notification

interface NotificationLifecycleManager {
    suspend fun syncOnAppLaunchIfSignedIn()

    suspend fun syncOnLoginSuccess()

    suspend fun syncOnNotificationPermissionGranted()

    suspend fun syncOnNewToken(token: String)
}
