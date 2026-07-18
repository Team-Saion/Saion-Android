package com.saion.feature.mypage.impl.mypage

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.domain.usecase.member.LogoutUseCase
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageEffect
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageIntent
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageSnackbarMessage
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageState
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPageViewModelTest {
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
    fun `내 정보 조회 성공 시 상태에 프로필 정보가 반영된다`() = runTest {
        val memberInfo = MemberInfo(
            memberId = "member-1",
            nickname = "수빈",
            profileImageUrl = "https://example.com/profile.png",
            avatarColorHex = "#6C757F",
            role = MemberRole.MEMBER,
            status = MemberStatus.ACTIVE,
        )
        val viewModel = MyPageViewModel(
            getMyInfoUseCase = GetMyInfoUseCase(
                FakeMemberRepository(myInfoResult = AppResult.Success(memberInfo)),
            ),
            logoutUseCase = LogoutUseCase(
                FakeMemberRepository(myInfoResult = AppResult.Success(memberInfo)),
            ),
        )

        advanceUntilIdle()

        assertEquals(
            MyPageState(
                nickname = "수빈",
                profileImageUrl = "https://example.com/profile.png",
                avatarColorHex = "#6C757F",
                isLoading = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `내 정보 조회 실패 시 스낵바 effect를 보낸다`() = runTest {
        val viewModel = MyPageViewModel(
            getMyInfoUseCase = GetMyInfoUseCase(
                FakeMemberRepository(
                    myInfoResult = AppResult.Failure(AppError.NetworkUnavailable()),
                ),
            ),
            logoutUseCase = LogoutUseCase(
                FakeMemberRepository(
                    myInfoResult = AppResult.Failure(AppError.NetworkUnavailable()),
                ),
            ),
        )
        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is MyPageEffect.ShowSnackbar)
        assertTrue((effect as MyPageEffect.ShowSnackbar).message is MyPageSnackbarMessage.Error)
        assertTrue(viewModel.uiState.value.isLoading.not())
    }

    @Test
    fun `로그아웃 메뉴 클릭 시 다이얼로그 상태가 열린다`() = runTest {
        val repository = FakeMemberRepository(myInfoResult = AppResult.Success(defaultMemberInfo()))
        val viewModel = MyPageViewModel(
            getMyInfoUseCase = GetMyInfoUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.dispatch(MyPageIntent.ClickLogout)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showLogoutDialog)
    }

    @Test
    fun `로그아웃 다이얼로그 취소 시 상태가 닫힌다`() = runTest {
        val repository = FakeMemberRepository(myInfoResult = AppResult.Success(defaultMemberInfo()))
        val viewModel = MyPageViewModel(
            getMyInfoUseCase = GetMyInfoUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.dispatch(MyPageIntent.ClickLogout)
        viewModel.dispatch(MyPageIntent.DismissLogoutDialog)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showLogoutDialog.not())
    }

    @Test
    fun `로그아웃 성공 시 다이얼로그를 닫고 완료 effect를 보낸다`() = runTest {
        val repository = FakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
            logoutResult = AppResult.Success(Unit),
        )
        val viewModel = MyPageViewModel(
            getMyInfoUseCase = GetMyInfoUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
        )
        advanceUntilIdle()
        viewModel.dispatch(MyPageIntent.ClickLogout)
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(MyPageIntent.ConfirmLogout)
        advanceUntilIdle()

        assertEquals(MyPageEffect.LogoutCompleted, effectDeferred.await())
        assertTrue(viewModel.uiState.value.showLogoutDialog.not())
        assertTrue(viewModel.uiState.value.isLogoutLoading.not())
    }

    @Test
    fun `로그아웃 실패 시 스낵바 effect를 보내고 다이얼로그를 유지한다`() = runTest {
        val repository = FakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
            logoutResult = AppResult.Failure(AppError.NetworkUnavailable()),
        )
        val viewModel = MyPageViewModel(
            getMyInfoUseCase = GetMyInfoUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
        )
        advanceUntilIdle()
        viewModel.dispatch(MyPageIntent.ClickLogout)
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(MyPageIntent.ConfirmLogout)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is MyPageEffect.ShowSnackbar)
        assertTrue((effect as MyPageEffect.ShowSnackbar).message is MyPageSnackbarMessage.Error)
        assertTrue(viewModel.uiState.value.showLogoutDialog)
        assertTrue(viewModel.uiState.value.isLogoutLoading.not())
    }

    @Test
    fun `프로필 새로고침 intent 시 내 정보를 다시 조회한다`() = runTest {
        val repository = FakeMemberRepository(
            myInfoResults = listOf(
                AppResult.Success(defaultMemberInfo()),
                AppResult.Success(defaultMemberInfo().copy(nickname = "변경됨")),
            ),
        )
        val viewModel = MyPageViewModel(
            getMyInfoUseCase = GetMyInfoUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
        )

        advanceUntilIdle()
        viewModel.dispatch(MyPageIntent.RefreshProfile)
        advanceUntilIdle()

        assertEquals("변경됨", viewModel.uiState.value.nickname)
        assertEquals(2, repository.getMyInfoCallCount)
    }
}

private fun defaultMemberInfo(): MemberInfo = MemberInfo(
    memberId = "member-1",
    nickname = "수빈",
    profileImageUrl = "https://example.com/profile.png",
    avatarColorHex = "#6C757F",
    role = MemberRole.MEMBER,
    status = MemberStatus.ACTIVE,
)

private class FakeMemberRepository(
    private val myInfoResult: AppResult<MemberInfo> = AppResult.Success(defaultMemberInfo()),
    private val myInfoResults: List<AppResult<MemberInfo>> = emptyList(),
    private val logoutResult: AppResult<Unit> = AppResult.Success(Unit),
) : MemberRepository {
    var getMyInfoCallCount: Int = 0
        private set

    override suspend fun getMyInfo(): AppResult<MemberInfo> {
        val result = myInfoResults.getOrNull(getMyInfoCallCount) ?: myInfoResult
        getMyInfoCallCount += 1
        return result
    }

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

    override suspend fun logout(): AppResult<Unit> = logoutResult

    override suspend fun withdraw(reason: String): AppResult<Unit> {
        throw UnsupportedOperationException()
    }
}
