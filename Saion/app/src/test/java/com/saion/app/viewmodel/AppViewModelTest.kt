package com.saion.app.viewmodel

import com.saion.app.navigation.startup.AppStartDestination
import com.saion.core.domain.repository.AuthRepository
import com.saion.core.domain.usecase.auth.ClearSessionUseCase
import com.saion.core.domain.usecase.auth.GetStoredMemberRoleUseCase
import com.saion.core.domain.usecase.auth.IsSignedInUseCase
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.auth.api.key.AuthStartStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
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
    fun `not signed in routes to splash then login`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(
            isSignedIn = false,
            storedMemberRole = AppResult.Success(MemberRole.MEMBER),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppStartDestination.SplashThenLogin, viewModel.uiState.value.startDestination)
        assertFalse(repository.clearSessionCalled)
    }

    @Test
    fun `pending role routes to terms`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(
            isSignedIn = true,
            storedMemberRole = AppResult.Success(MemberRole.PENDING),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            AppStartDestination.Auth(startStep = AuthStartStep.TERMS),
            viewModel.uiState.value.startDestination,
        )
        assertFalse(repository.clearSessionCalled)
    }

    @Test
    fun `member role routes to main`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(
            isSignedIn = true,
            storedMemberRole = AppResult.Success(MemberRole.MEMBER),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppStartDestination.Main, viewModel.uiState.value.startDestination)
        assertFalse(repository.clearSessionCalled)
    }

    @Test
    fun `admin role routes to main`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(
            isSignedIn = true,
            storedMemberRole = AppResult.Success(MemberRole.ADMIN),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppStartDestination.Main, viewModel.uiState.value.startDestination)
        assertFalse(repository.clearSessionCalled)
    }

    @Test
    fun `stored role failure clears session and routes to login`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(
            isSignedIn = true,
            storedMemberRole = AppResult.Failure(AppError.Unknown(message = "invalid token")),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppStartDestination.SplashThenLogin, viewModel.uiState.value.startDestination)
        assertTrue(repository.clearSessionCalled)
    }
}

private fun createViewModel(repository: FakeAuthRepository): AppViewModel = AppViewModel(
    isSignedInUseCase = IsSignedInUseCase(authRepository = repository),
    getStoredMemberRoleUseCase = GetStoredMemberRoleUseCase(authRepository = repository),
    clearSessionUseCase = ClearSessionUseCase(authRepository = repository),
)

private class FakeAuthRepository(
    private val isSignedIn: Boolean,
    private val storedMemberRole: AppResult<MemberRole>,
) : AuthRepository {
    var clearSessionCalled: Boolean = false

    override suspend fun loginWithKakao(idToken: String): AppResult<MemberRole> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override suspend fun getStoredMemberRole(): AppResult<MemberRole> = storedMemberRole

    override suspend fun isSignedIn(): Boolean = isSignedIn

    override suspend fun clearSession() {
        clearSessionCalled = true
    }
}
