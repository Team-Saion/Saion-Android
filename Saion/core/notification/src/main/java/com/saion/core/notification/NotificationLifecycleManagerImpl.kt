package com.saion.core.notification

import com.saion.core.domain.usecase.auth.IsSignedInUseCase
import com.saion.core.domain.usecase.push.RegisterPushTokenUseCase
import com.saion.core.model.push.PushPlatform
import com.saion.core.model.push.RegisterPushTokenCommand
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import timber.log.Timber

class NotificationLifecycleManagerImpl @Inject constructor(
    private val isSignedInUseCase: IsSignedInUseCase,
    private val registerPushTokenUseCase: RegisterPushTokenUseCase,
    private val fcmTokenProvider: FcmTokenProvider,
    private val notificationPermissionStatusProvider: NotificationPermissionStatusProvider,
    private val notificationAppInfoProvider: NotificationAppInfoProvider,
) : NotificationLifecycleManager {
    override suspend fun syncOnAppLaunchIfSignedIn() {
        if (!isSignedInUseCase()) return
        syncCurrentToken()
    }

    override suspend fun syncOnLoginSuccess() {
        syncCurrentToken()
    }

    override suspend fun syncOnNotificationPermissionGranted() {
        syncCurrentToken()
    }

    override suspend fun syncOnNewToken(token: String) {
        if (!isSignedInUseCase()) {
            Timber.tag(TAG).d("Skip push token sync because session is not available.")
            return
        }
        registerToken(token)
    }

    private suspend fun syncCurrentToken() {
        val token = runCatching { fcmTokenProvider.getToken() }
            .getOrElse { throwable ->
                if (throwable is CancellationException) throw throwable
                Timber.tag(TAG).w(throwable, "Failed to fetch FCM token.")
                return
            }
        registerToken(token)
    }

    private suspend fun registerToken(token: String) {
        when (
            val result = registerPushTokenUseCase(
                command = RegisterPushTokenCommand(
                    token = token,
                    platform = PushPlatform.ANDROID,
                    osNotificationPermissionGranted = notificationPermissionStatusProvider.isGranted(),
                    appVersion = notificationAppInfoProvider.versionName(),
                ),
            )
        ) {
            is AppResult.Success -> Unit
            is AppResult.Failure -> {
                Timber.tag(TAG).w("Push token register failed: %s", result.error.describe())
            }
        }
    }

    private companion object {
        const val TAG = "NotificationLifecycle"
    }
}

private fun AppError.describe(): String = when (this) {
    is AppError.Business -> message.orEmpty().ifBlank { rawCode }
    is AppError.Unknown -> message.orEmpty().ifBlank { "unknown" }
    is AppError.NetworkUnavailable -> "network-unavailable"
    is AppError.Timeout -> "timeout"
    is AppError.Unauthorized -> "unauthorized"
    is AppError.ServerUnavailable -> "server-unavailable"
}
