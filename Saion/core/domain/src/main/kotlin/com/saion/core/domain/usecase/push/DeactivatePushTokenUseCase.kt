package com.saion.core.domain.usecase.push

import com.saion.core.domain.repository.PushTokenRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class DeactivatePushTokenUseCase @Inject constructor(
    private val pushTokenRepository: PushTokenRepository,
) {
    suspend operator fun invoke(tokenId: Long): AppResult<Unit> = pushTokenRepository.deactivatePushToken(tokenId = tokenId)
}
