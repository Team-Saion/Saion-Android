package com.saion.feature.auth.impl.login

import android.content.Context
import com.saion.core.auth.SocialAuthActionResult
import com.saion.core.auth.SocialAuthClient
import com.saion.core.auth.SocialAuthProvider
import com.saion.core.auth.SocialLoginFailureReason
import com.saion.core.auth.SocialLoginResult
import com.saion.core.domain.repository.AuthRepository
import com.saion.core.domain.usecase.auth.LoginWithKakaoUseCase
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
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
import org.junit.Assert.assertNull
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
    fun `login success emits navigate effect`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            socialAuthClient = FakeSocialAuthClient(
                loginResult = SocialLoginResult.Success(
                    provider = SocialAuthProvider.KAKAO,
                    idToken = "token",
                ),
            ),
            loginWithKakaoUseCase = LoginWithKakaoUseCase(
                authRepository = FakeAuthRepository(
                    loginResult = AppResult.Success(Unit),
                ),
            ),
        )
        val effect = CompletableDeferred<LoginEffect>()
        backgroundScope.launch {
            effect.complete(viewModel.uiEffect.first())
        }

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(LoginEffect.NavigateNext, effect.await())
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `social login failure updates error message`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            socialAuthClient = FakeSocialAuthClient(
                loginResult = SocialLoginResult.Failure(
                    reason = SocialLoginFailureReason.SDK_ERROR,
                    message = "sdk failed",
                ),
            ),
            loginWithKakaoUseCase = LoginWithKakaoUseCase(
                authRepository = FakeAuthRepository(
                    loginResult = AppResult.Success(Unit),
                ),
            ),
        )

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("sdk failed", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `backend login failure updates error message`() = runTest(dispatcher) {
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

        viewModel.login(context)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("login failed", viewModel.uiState.value.errorMessage)
    }
}

private class FakeSocialAuthClient(private val loginResult: SocialLoginResult) : SocialAuthClient {
    override suspend fun login(
        provider: SocialAuthProvider,
        context: Context,
    ): SocialLoginResult = loginResult

    override suspend fun logout(provider: SocialAuthProvider): SocialAuthActionResult = SocialAuthActionResult.Success
}

private class FakeAuthRepository(private val loginResult: AppResult<Unit>) : AuthRepository {
    override suspend fun loginWithKakao(idToken: String): AppResult<Unit> = loginResult

    override suspend fun isSignedIn(): Boolean = false
}
