package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.PushTokenRepository
import com.saion.core.model.push.PushPlatform
import com.saion.core.model.push.PushToken
import com.saion.core.model.push.RegisterPushTokenCommand
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.PushTokenRemoteDataSource
import com.saion.core.network.model.push.PushTokenResponse
import javax.inject.Inject

internal class PushTokenRepositoryImpl @Inject constructor(
    private val pushTokenRemoteDataSource: PushTokenRemoteDataSource,
) : PushTokenRepository {
    override suspend fun registerPushToken(command: RegisterPushTokenCommand): AppResult<PushToken> = safeRequest(
        request = {
            pushTokenRemoteDataSource.register(
                token = command.token,
                platform = command.platform.value,
                osNotificationPermissionGranted = command.osNotificationPermissionGranted,
                appVersion = command.appVersion,
            )
        },
    ) { response ->
        response.toDomain()
    }

    override suspend fun deactivatePushToken(tokenId: Long): AppResult<Unit> = safeRequest(
        request = { pushTokenRemoteDataSource.deactivate(tokenId = tokenId) },
    ) {
        AppResult.Success(Unit)
    }
}

private fun PushTokenResponse.toDomain(): AppResult<PushToken> {
    val pushPlatform = PushPlatform.from(platform)
        ?: return AppResult.Failure(
            AppError.Unknown(message = "Push platform is missing or invalid."),
        )

    return AppResult.Success(
        PushToken(
            id = id,
            platform = pushPlatform,
            osNotificationPermissionGranted = osNotificationPermissionGranted,
            appVersion = appVersion,
            active = active,
        ),
    )
}
