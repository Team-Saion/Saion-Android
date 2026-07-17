package com.saion.feature.mypage.impl.mypage

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageEffect
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
        )
        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is MyPageEffect.ShowSnackbar)
        assertTrue((effect as MyPageEffect.ShowSnackbar).message is MyPageSnackbarMessage.Error)
        assertTrue(viewModel.uiState.value.isLoading.not())
    }
}

private class FakeMemberRepository(private val myInfoResult: AppResult<MemberInfo>) : MemberRepository {
    override suspend fun getMyInfo(): AppResult<MemberInfo> = myInfoResult

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
        throw UnsupportedOperationException()
    }
}
