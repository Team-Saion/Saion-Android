package com.saion.core.domain.usecase.push

import com.saion.core.domain.repository.PushTokenRepository
import com.saion.core.model.push.PushPlatform
import com.saion.core.model.push.PushToken
import com.saion.core.model.push.RegisterPushTokenCommand
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PushTokenUseCasesTest {
    @Test
    fun `푸시 토큰 등록은 command를 그대로 저장소에 전달한다`() = runBlocking {
        val command = defaultRegisterPushTokenCommand()
        val expected = AppResult.Success(defaultPushToken())
        val repository = FakePushTokenRepository(registerResult = expected)

        val actual = RegisterPushTokenUseCase(repository).invoke(command = command)

        assertEquals(
            PushTokenUseCaseOutcome(
                result = expected,
                call = PushTokenRepositoryCall.Register(command = command),
            ),
            PushTokenUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `푸시 토큰 비활성화는 tokenId를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(Unit)
        val repository = FakePushTokenRepository(deactivateResult = expected)

        val actual = DeactivatePushTokenUseCase(repository).invoke(tokenId = 3L)

        assertEquals(
            PushTokenUseCaseOutcome(
                result = expected,
                call = PushTokenRepositoryCall.Deactivate(tokenId = 3L),
            ),
            PushTokenUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

}

private data class PushTokenUseCaseOutcome<T>(
    val result: AppResult<T>,
    val call: PushTokenRepositoryCall?,
)

private sealed interface PushTokenRepositoryCall {
    data class Register(val command: RegisterPushTokenCommand) : PushTokenRepositoryCall

    data class Deactivate(val tokenId: Long) : PushTokenRepositoryCall
}

private class FakePushTokenRepository(
    private val registerResult: AppResult<PushToken> = AppResult.Success(defaultPushToken()),
    private val deactivateResult: AppResult<Unit> = AppResult.Success(Unit),
) : PushTokenRepository {
    var lastCall: PushTokenRepositoryCall? = null

    override suspend fun registerPushToken(command: RegisterPushTokenCommand): AppResult<PushToken> {
        lastCall = PushTokenRepositoryCall.Register(command = command)
        return registerResult
    }

    override suspend fun deactivatePushToken(tokenId: Long): AppResult<Unit> {
        lastCall = PushTokenRepositoryCall.Deactivate(tokenId = tokenId)
        return deactivateResult
    }
}

private fun defaultRegisterPushTokenCommand(): RegisterPushTokenCommand = RegisterPushTokenCommand(
    installationId = "installation-id",
    token = "fcm-token",
    platform = PushPlatform.ANDROID,
)

private fun defaultPushToken(): PushToken = PushToken(
    id = 3L,
    platform = PushPlatform.ANDROID,
    active = true,
)
