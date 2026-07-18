package com.saion.feature.mypage.impl.withdraw

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.domain.usecase.member.WithdrawUseCase
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawEffect
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawIntent
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawSnackbarMessage
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawState
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WithdrawViewModelTest {
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
    fun `사유 입력 시 상태가 갱신된다`() = runTest {
        val repository = FakeMemberRepository()
        val viewModel = WithdrawViewModel(
            withdrawUseCase = WithdrawUseCase(repository),
        )

        viewModel.dispatch(WithdrawIntent.ReasonChanged("광고 알림이 많아요"))
        advanceUntilIdle()

        assertEquals(
            WithdrawState(reason = "광고 알림이 많아요"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `탈퇴 버튼 클릭 시 확인 다이얼로그가 열린다`() = runTest {
        val repository = FakeMemberRepository()
        val viewModel = WithdrawViewModel(
            withdrawUseCase = WithdrawUseCase(repository),
        )

        viewModel.dispatch(WithdrawIntent.ClickWithdraw)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showWithdrawDialog)
    }

    @Test
    fun `탈퇴 다이얼로그 취소 시 닫힌다`() = runTest {
        val repository = FakeMemberRepository()
        val viewModel = WithdrawViewModel(
            withdrawUseCase = WithdrawUseCase(repository),
        )

        viewModel.dispatch(WithdrawIntent.ClickWithdraw)
        viewModel.dispatch(WithdrawIntent.DismissWithdrawDialog)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showWithdrawDialog.not())
        assertTrue(viewModel.uiState.value.isSubmitting.not())
    }

    @Test
    fun `탈퇴 성공 시 완료 effect를 보낸다`() = runTest {
        val repository = FakeMemberRepository(withdrawResult = AppResult.Success(Unit))
        val viewModel = WithdrawViewModel(
            withdrawUseCase = WithdrawUseCase(repository),
        )
        viewModel.dispatch(WithdrawIntent.ReasonChanged("일정이 복잡해요"))
        viewModel.dispatch(WithdrawIntent.ClickWithdraw)
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(WithdrawIntent.ConfirmWithdraw)
        advanceUntilIdle()

        assertEquals(WithdrawEffect.WithdrawCompleted, effectDeferred.await())
        assertEquals("일정이 복잡해요", repository.lastWithdrawReason)
        assertTrue(viewModel.uiState.value.showWithdrawDialog.not())
        assertTrue(viewModel.uiState.value.isSubmitting.not())
    }

    @Test
    fun `탈퇴 실패 시 스낵바 effect를 보내고 다이얼로그를 유지한다`() = runTest {
        val repository = FakeMemberRepository(
            withdrawResult = AppResult.Failure(AppError.NetworkUnavailable()),
        )
        val viewModel = WithdrawViewModel(
            withdrawUseCase = WithdrawUseCase(repository),
        )
        viewModel.dispatch(WithdrawIntent.ClickWithdraw)
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(WithdrawIntent.ConfirmWithdraw)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is WithdrawEffect.ShowSnackbar)
        assertTrue((effect as WithdrawEffect.ShowSnackbar).message is WithdrawSnackbarMessage.Error)
        assertTrue(viewModel.uiState.value.showWithdrawDialog)
        assertTrue(viewModel.uiState.value.isSubmitting.not())
    }

    @Test
    fun `제출 중 중복 confirm은 무시된다`() = runTest {
        val repository = FakeMemberRepository(
            withdrawResult = AppResult.Success(Unit),
            suspendWithdraw = true,
        )
        val viewModel = WithdrawViewModel(
            withdrawUseCase = WithdrawUseCase(repository),
        )
        viewModel.dispatch(WithdrawIntent.ClickWithdraw)
        advanceUntilIdle()

        viewModel.dispatch(WithdrawIntent.ConfirmWithdraw)
        advanceUntilIdle()
        viewModel.dispatch(WithdrawIntent.ConfirmWithdraw)
        advanceUntilIdle()

        assertEquals(1, repository.withdrawCallCount)
    }

    @Test
    fun `빈 사유는 사유 없음으로 전달된다`() = runTest {
        val repository = FakeMemberRepository(withdrawResult = AppResult.Success(Unit))
        val viewModel = WithdrawViewModel(
            withdrawUseCase = WithdrawUseCase(repository),
        )
        viewModel.dispatch(WithdrawIntent.ClickWithdraw)
        advanceUntilIdle()

        viewModel.dispatch(WithdrawIntent.ConfirmWithdraw)
        advanceUntilIdle()

        assertEquals("사유 없음", repository.lastWithdrawReason)
    }

    @Test
    fun `공백만 있는 사유도 사유 없음으로 전달된다`() = runTest {
        val repository = FakeMemberRepository(withdrawResult = AppResult.Success(Unit))
        val viewModel = WithdrawViewModel(
            withdrawUseCase = WithdrawUseCase(repository),
        )
        viewModel.dispatch(WithdrawIntent.ReasonChanged("   "))
        viewModel.dispatch(WithdrawIntent.ClickWithdraw)
        advanceUntilIdle()

        viewModel.dispatch(WithdrawIntent.ConfirmWithdraw)
        advanceUntilIdle()

        assertEquals("사유 없음", repository.lastWithdrawReason)
    }
}

private class FakeMemberRepository(
    private val withdrawResult: AppResult<Unit> = AppResult.Success(Unit),
    private val suspendWithdraw: Boolean = false,
) : MemberRepository {
    var withdrawCallCount: Int = 0
        private set

    var lastWithdrawReason: String? = null
        private set

    override fun observeMyInfo(): Flow<MemberInfo?> = flowOf(null)

    override suspend fun getMyInfo(): AppResult<MemberInfo> {
        throw UnsupportedOperationException()
    }

    override suspend fun refreshMyInfo(): AppResult<MemberInfo> = getMyInfo()

    override suspend fun getOnboardingInfo(): AppResult<OnboardingInfo> {
        throw UnsupportedOperationException()
    }

    override suspend fun completeOnboarding(nickname: String): AppResult<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun updateProfile(nickname: String): AppResult<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun changeState(
        status: MemberStatus?,
        role: MemberRole?,
    ): AppResult<MemberInfo> {
        throw UnsupportedOperationException()
    }

    override suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun logout(): AppResult<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun withdraw(reason: String): AppResult<Unit> {
        withdrawCallCount += 1
        lastWithdrawReason = reason
        if (suspendWithdraw) {
            kotlinx.coroutines.awaitCancellation()
        }
        return withdrawResult
    }
}
