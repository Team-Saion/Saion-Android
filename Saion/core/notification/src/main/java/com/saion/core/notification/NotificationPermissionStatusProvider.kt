package com.saion.core.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationPermissionStatusProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun isGranted(): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()
}
