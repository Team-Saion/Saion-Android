package com.saion.feature.auth.impl.login

import android.content.Context
import android.content.ContextWrapper
import com.saion.core.auth.SocialAuthActionResult
import com.saion.core.auth.SocialAuthClient
import com.saion.core.auth.SocialAuthProvider
import com.saion.core.auth.SocialLoginResult
import com.saion.core.domain.repository.AuthRepository
import com.saion.core.domain.usecase.auth.LoginWithKakaoUseCase
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult
import com.saion.core.notification.NotificationLifecycleManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `로그인 성공 시 푸시 토큰 동기화를 요청하고 메인 이동 effect를 보낸다`() = runTest(dispatcher) {
        val authClient = FakeSocialAuthClient(
            result = SocialLoginResult.Success(
                provider = SocialAuthProvider.KAKAO,
                idToken = "id-token",
            ),
        )
        val repository = FakeAuthRepository(result = AppResult.Success(MemberRole.MEMBER))
        val notificationManager = FakeNotificationLifecycleManager()
        val viewModel = LoginViewModel(
            socialAuthClient = authClient,
            loginWithKakaoUseCase = LoginWithKakaoUseCase(repository),
            notificationLifecycleManager = notificationManager,
        )
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.login(context = ContextWrapper(null))
        advanceUntilIdle()

        assertEquals(LoginEffect.NavigateMain, effectDeferred.await())
        assertEquals(1, notificationManager.syncOnLoginSuccessCallCount)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}

private class FakeSocialAuthClient(
    private val result: SocialLoginResult,
) : SocialAuthClient {
    override suspend fun login(
        provider: SocialAuthProvider,
        context: Context,
    ): SocialLoginResult = result

    override suspend fun logout(provider: SocialAuthProvider): SocialAuthActionResult = SocialAuthActionResult.Success
}

private class FakeAuthRepository(
    private val result: AppResult<MemberRole>,
) : AuthRepository {
    override suspend fun loginWithKakao(idToken: String): AppResult<MemberRole> = result

    override suspend fun getStoredMemberRole(): AppResult<MemberRole> = error("Not required for this test")

    override suspend fun isSignedIn(): Boolean = error("Not required for this test")

    override suspend fun clearSession() = error("Not required for this test")
}

private class FakeNotificationLifecycleManager : NotificationLifecycleManager {
    var syncOnLoginSuccessCallCount: Int = 0

    override suspend fun syncOnAppLaunchIfSignedIn() = error("Not required for this test")

    override suspend fun syncOnLoginSuccess() {
        syncOnLoginSuccessCallCount += 1
    }

    override suspend fun syncOnNotificationPermissionGranted() = error("Not required for this test")

    override suspend fun syncOnNewToken(token: String) = error("Not required for this test")
}
