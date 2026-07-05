package com.saion.feature.auth.impl.login

import android.content.Context
import com.saion.core.auth.SocialAuthActionResult
import com.saion.core.auth.SocialAuthClient
import com.saion.core.auth.SocialAuthProvider
import com.saion.core.auth.SocialLoginFailureReason
import com.saion.core.auth.SocialLoginResult
import com.saion.core.domain.repository.AuthRepository
import com.saion.core.domain.usecase.auth.LoginWithKakaoUseCase
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.auth.api.key.AuthStartStep
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val context: Context = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success with pending role emits terms navigation effect`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            socialAuthClient = FakeSocialAuthClient(
                loginResult = SocialLoginResult.Success(
                    provider = SocialAuthProvider.KAKAO,
                    idToken = "token",
                ),
            ),
            loginWithKakaoUseCase = LoginWithKakaoUseCase(
                authRepository = FakeAuthRepository(
                    loginResult = AppResult.Success(MemberRole.PENDING),
                ),
            ),
        )
        val effect = CompletableDeferred<LoginEffect>()
        backgroundScope.launch {
            effect.complete(viewModel.uiEffect.first())
        }

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(LoginEffect.NavigateNext(AuthStartStep.TERMS), effect.await())
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login success with member role emits main navigation effect`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            socialAuthClient = FakeSocialAuthClient(
                loginResult = SocialLoginResult.Success(
                    provider = SocialAuthProvider.KAKAO,
                    idToken = "token",
                ),
            ),
            loginWithKakaoUseCase = LoginWithKakaoUseCase(
                authRepository = FakeAuthRepository(
                    loginResult = AppResult.Success(MemberRole.MEMBER),
                ),
            ),
        )
        val effect = CompletableDeferred<LoginEffect>()
        backgroundScope.launch {
            effect.complete(viewModel.uiEffect.first())
        }

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(LoginEffect.NavigateMain, effect.await())
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login success with admin role emits main navigation effect`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            socialAuthClient = FakeSocialAuthClient(
                loginResult = SocialLoginResult.Success(
                    provider = SocialAuthProvider.KAKAO,
                    idToken = "token",
                ),
            ),
            loginWithKakaoUseCase = LoginWithKakaoUseCase(
                authRepository = FakeAuthRepository(
                    loginResult = AppResult.Success(MemberRole.ADMIN),
                ),
            ),
        )
        val effect = CompletableDeferred<LoginEffect>()
        backgroundScope.launch {
            effect.complete(viewModel.uiEffect.first())
        }

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(LoginEffect.NavigateMain, effect.await())
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `social login cancellation emits snackbar effect`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            socialAuthClient = FakeSocialAuthClient(
                loginResult = SocialLoginResult.Cancelled,
            ),
            loginWithKakaoUseCase = LoginWithKakaoUseCase(
                authRepository = FakeAuthRepository(
                    loginResult = AppResult.Success(MemberRole.MEMBER),
                ),
            ),
        )
        val effect = CompletableDeferred<LoginEffect>()
        backgroundScope.launch {
            effect.complete(viewModel.uiEffect.first())
        }

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            LoginEffect.ShowSnackbar("카카오 로그인이 취소되었습니다."),
            effect.await(),
        )
    }

    @Test
    fun `social login failure emits snackbar effect`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            socialAuthClient = FakeSocialAuthClient(
                loginResult = SocialLoginResult.Failure(
                    reason = SocialLoginFailureReason.SDK_ERROR,
                    message = "sdk failed",
                ),
            ),
            loginWithKakaoUseCase = LoginWithKakaoUseCase(
                authRepository = FakeAuthRepository(
                    loginResult = AppResult.Success(MemberRole.MEMBER),
                ),
            ),
        )
        val effect = CompletableDeferred<LoginEffect>()
        backgroundScope.launch {
            effect.complete(viewModel.uiEffect.first())
        }

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(LoginEffect.ShowSnackbar("sdk failed"), effect.await())
    }

    @Test
    fun `backend login failure emits snackbar effect`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            socialAuthClient = FakeSocialAuthClient(
                loginResult = SocialLoginResult.Success(
                    provider = SocialAuthProvider.KAKAO,
                    idToken = "token",
                ),
            ),
            loginWithKakaoUseCase = LoginWithKakaoUseCase(
                authRepository = FakeAuthRepository(
                    loginResult = AppResult.Failure(
                        AppError.Unknown(message = "login failed"),
                    ),
                ),
            ),
        )
        val effect = CompletableDeferred<LoginEffect>()
        backgroundScope.launch {
            effect.complete(viewModel.uiEffect.first())
        }

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(LoginEffect.ShowSnackbar("login failed"), effect.await())
    }
}

private class FakeSocialAuthClient(private val loginResult: SocialLoginResult) : SocialAuthClient {
    override suspend fun login(
        provider: SocialAuthProvider,
        context: Context,
    ): SocialLoginResult = loginResult

    override suspend fun logout(provider: SocialAuthProvider): SocialAuthActionResult = SocialAuthActionResult.Success
}

private class FakeAuthRepository(private val loginResult: AppResult<MemberRole>) : AuthRepository {
    override suspend fun loginWithKakao(idToken: String): AppResult<MemberRole> = loginResult

    override suspend fun getStoredMemberRole(): AppResult<MemberRole> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override suspend fun isSignedIn(): Boolean = false

    override suspend fun clearSession() = Unit
}
