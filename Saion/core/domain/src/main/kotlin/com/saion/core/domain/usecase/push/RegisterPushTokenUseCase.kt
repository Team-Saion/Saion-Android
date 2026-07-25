package com.saion.core.domain.usecase.push

import com.saion.core.domain.repository.PushTokenRepository
import com.saion.core.model.push.PushToken
import com.saion.core.model.push.RegisterPushTokenCommand
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 기기의 FCM 토큰을 등록합니다.
 *
 * 설치 식별자와 함께 등록해 서버가 디바이스 단위로 토큰을 추적할 수 있게 합니다.
 */
class RegisterPushTokenUseCase @Inject constructor(
    private val pushTokenRepository: PushTokenRepository,
) {
    suspend operator fun invoke(command: RegisterPushTokenCommand): AppResult<PushToken> =
        pushTokenRepository.registerPushToken(command = command)
}
