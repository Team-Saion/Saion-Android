package com.saion.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.saion.core.notification.FcmPayloadKeys.EVENT_TYPE
import com.saion.core.notification.FcmPayloadKeys.SCHEDULE_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SaionFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var notificationLifecycleManager: NotificationLifecycleManager

    @Inject
    lateinit var notificationPermissionStatusProvider: NotificationPermissionStatusProvider

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            notificationLifecycleManager.syncOnNewToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message)
    }

    private fun showNotification(message: RemoteMessage) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent == null) {
            Timber.w("Unable to create launch intent for FCM notification.")
            return
        }

        val title = message.notification?.title?.takeIf(String::isNotBlank)
            ?: applicationInfo.loadLabel(packageManager).toString()
        val body = message.notification?.body
            ?.takeIf(String::isNotBlank)
            ?: message.data["body"].orEmpty()
        if (title.isBlank() && body.isBlank()) {
            Timber.d("Skipping empty FCM notification payload.")
            return
        }
        if (!notificationPermissionStatusProvider.isGranted()) {
            Timber.d("Skipping FCM notification because notifications are disabled.")
            return
        }

        ensureNotificationChannel()

        val contentIntent = PendingIntent.getActivity(
            this,
            message.data.hashCode(),
            launchIntent.apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                message.data.forEach { (key, value) ->
                    putExtra(key, value)
                }
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(applicationInfo.icon)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(
            message.notificationId(),
            notification,
        )
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            applicationInfo.loadLabel(packageManager).toString(),
            NotificationManager.IMPORTANCE_HIGH,
        )
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private fun RemoteMessage.notificationId(): Int {
        val eventType = data[EVENT_TYPE].orEmpty()
        val scheduleId = data[SCHEDULE_ID].orEmpty()
        return "$eventType:$scheduleId:${messageId.orEmpty()}".hashCode()
    }

    private companion object {
        const val NOTIFICATION_CHANNEL_ID: String = "saion_default_notification"
    }
}
