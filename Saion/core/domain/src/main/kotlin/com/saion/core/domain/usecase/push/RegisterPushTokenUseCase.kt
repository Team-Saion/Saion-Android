package com.saion.core.domain.usecase.push

import com.saion.core.domain.repository.PushTokenRepository
import com.saion.core.model.push.PushToken
import com.saion.core.model.push.RegisterPushTokenCommand
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 기기의 FCM 토큰을 등록합니다.
 *
 * OS 알림 권한 상태와 앱 버전까지 함께 전달해 서버가 발송 대상을 판단할 수 있게 합니다.
 */
class RegisterPushTokenUseCase @Inject constructor(
    private val pushTokenRepository: PushTokenRepository,
) {
    suspend operator fun invoke(command: RegisterPushTokenCommand): AppResult<PushToken> =
        pushTokenRepository.registerPushToken(command = command)
}
