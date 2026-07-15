package com.saion.core.domain.usecase.push

import com.saion.core.domain.repository.PushTokenRepository
import com.saion.core.model.push.PushToken
import com.saion.core.model.push.RegisterPushTokenCommand
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class RegisterPushTokenUseCase @Inject constructor(
    private val pushTokenRepository: PushTokenRepository,
) {
    suspend operator fun invoke(command: RegisterPushTokenCommand): AppResult<PushToken> =
        pushTokenRepository.registerPushToken(command = command)
}
