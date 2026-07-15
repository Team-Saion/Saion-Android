package com.saion.core.domain.repository

import com.saion.core.model.push.PushToken
import com.saion.core.model.push.RegisterPushTokenCommand
import com.saion.core.model.result.AppResult

interface PushTokenRepository {
    suspend fun registerPushToken(command: RegisterPushTokenCommand): AppResult<PushToken>

    suspend fun deactivatePushToken(tokenId: Long): AppResult<Unit>
}
