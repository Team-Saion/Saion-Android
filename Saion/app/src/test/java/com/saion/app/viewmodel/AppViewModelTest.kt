package com.saion.app.viewmodel

import com.saion.app.navigation.startup.AppStartDestination
import com.saion.core.domain.repository.AuthRepository
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.usecase.auth.ClearSessionUseCase
import com.saion.core.domain.usecase.auth.GetStoredMemberRoleUseCase
import com.saion.core.domain.usecase.auth.IsSignedInUseCase
import com.saion.core.domain.usecase.circle.SyncCurrentCircleUseCase
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.auth.api.key.AuthStartStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
        val repository = FakeRepository(
            isSignedIn = false,
            storedMemberRole = AppResult.Success(MemberRole.MEMBER),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppStartDestination.SplashThenLogin, viewModel.uiState.value.startDestination)
        assertFalse(repository.clearSessionCalled)
        assertEquals(0, repository.syncCurrentCircleCallCount)
    }

    @Test
    fun `pending role routes to terms`() = runTest(dispatcher) {
        val repository = FakeRepository(
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
        assertEquals(0, repository.syncCurrentCircleCallCount)
    }

    @Test
    fun `member role routes to main and syncs current circle`() = runTest(dispatcher) {
        val repository = FakeRepository(
            isSignedIn = true,
            storedMemberRole = AppResult.Success(MemberRole.MEMBER),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppStartDestination.Main, viewModel.uiState.value.startDestination)
        assertFalse(repository.clearSessionCalled)
        assertEquals(1, repository.syncCurrentCircleCallCount)
    }

    @Test
    fun `admin role routes to main and syncs current circle`() = runTest(dispatcher) {
        val repository = FakeRepository(
            isSignedIn = true,
            storedMemberRole = AppResult.Success(MemberRole.ADMIN),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppStartDestination.Main, viewModel.uiState.value.startDestination)
        assertFalse(repository.clearSessionCalled)
        assertEquals(1, repository.syncCurrentCircleCallCount)
    }

    @Test
    fun `stored role failure clears session and routes to login`() = runTest(dispatcher) {
        val repository = FakeRepository(
            isSignedIn = true,
            storedMemberRole = AppResult.Failure(AppError.Unknown(message = "invalid token")),
        )

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppStartDestination.SplashThenLogin, viewModel.uiState.value.startDestination)
        assertTrue(repository.clearSessionCalled)
        assertEquals(0, repository.syncCurrentCircleCallCount)
    }
}

private fun createViewModel(repository: FakeRepository): AppViewModel = AppViewModel(
    isSignedInUseCase = IsSignedInUseCase(authRepository = repository),
    getStoredMemberRoleUseCase = GetStoredMemberRoleUseCase(authRepository = repository),
    clearSessionUseCase = ClearSessionUseCase(authRepository = repository),
    syncCurrentCircleUseCase = SyncCurrentCircleUseCase(currentCircleRepository = repository),
)

private class FakeRepository(
    private val isSignedIn: Boolean,
    private val storedMemberRole: AppResult<MemberRole>,
) : AuthRepository, CurrentCircleRepository {
    var clearSessionCalled: Boolean = false
    var syncCurrentCircleCallCount: Int = 0

    override suspend fun loginWithKakao(idToken: String): AppResult<MemberRole> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override suspend fun getStoredMemberRole(): AppResult<MemberRole> = storedMemberRole

    override suspend fun isSignedIn(): Boolean = isSignedIn

    override suspend fun clearSession() {
        clearSessionCalled = true
    }

    override fun observeCurrentCircleId(): Flow<String?> = flowOf(null)

    override suspend fun getCurrentCircleId(): String? = null

    override suspend fun selectCircle(circleId: String): AppResult<Unit> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override suspend fun syncCurrentCircle(): AppResult<String?> {
        syncCurrentCircleCallCount += 1
        return AppResult.Success("circle-1")
    }

    override suspend fun clearCurrentCircle() = Unit
}
