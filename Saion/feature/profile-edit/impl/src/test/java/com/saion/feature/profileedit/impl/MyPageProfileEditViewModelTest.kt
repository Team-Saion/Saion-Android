package com.saion.feature.profileedit.impl

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.domain.usecase.member.UpdateMyProfileWithProfileImageUseCase
import com.saion.core.domain.usecase.member.UpdateProfileUseCase
import com.saion.core.domain.usecase.member.UploadProfileImageUseCase
import com.saion.core.domain.usecase.member.ValidateNicknameUseCase
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
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
class MyPageProfileEditViewModelTest {
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
    fun `내 정보 조회 성공 시 프로필 수정 초기 상태가 반영된다`() = runTest {
        val repository = MyPageProfileEditFakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
        )
        val viewModel = createMyPageProfileEditViewModel(repository = repository)

        advanceUntilIdle()

        assertEquals(
            ProfileEditUiState(
                initialNickname = "수빈",
                nicknamePlaceholder = "수빈",
                nickname = "수빈",
                validation = ValidateNicknameUseCase()("수빈"),
                profileImageUrl = "https://example.com/profile.png",
                avatarColorHex = "#6C757F",
                isSubmitting = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `프로필 저장 성공 시 뒤로가기 effect를 보낸다`() = runTest {
        val repository = MyPageProfileEditFakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
        )
        val viewModel = createMyPageProfileEditViewModel(repository = repository)
        advanceUntilIdle()
        viewModel.dispatch(ProfileEditIntent.NicknameChanged("새닉네임"))
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(ProfileEditIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(ProfileEditEffect.NavigateBack, effectDeferred.await())
        assertEquals(listOf("update:새닉네임"), repository.submitCallLog)
    }

    @Test
    fun `초기 로드 직후에는 저장 버튼이 비활성화된다`() = runTest {
        val repository = MyPageProfileEditFakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
        )
        val viewModel = createMyPageProfileEditViewModel(repository = repository)

        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isSubmitEnabled)
    }

    @Test
    fun `이미지를 선택하면 로컬 프로필 이미지 상태가 반영된다`() = runTest {
        val repository = MyPageProfileEditFakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
        )
        val viewModel = createMyPageProfileEditViewModel(repository = repository)

        advanceUntilIdle()
        viewModel.dispatch(ProfileEditIntent.ProfileImageSelected("content://picked/profile.png"))
        advanceUntilIdle()

        assertEquals("content://picked/profile.png", viewModel.uiState.value.selectedProfileImageUri)
    }

    @Test
    fun `이미지를 선택한 뒤 저장하면 업로드 후 프로필 수정을 호출한다`() = runTest {
        val repository = MyPageProfileEditFakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
        )
        val viewModel = createMyPageProfileEditViewModel(
            repository = repository,
            profileImageReader = LoggingProfileImageReader(
                submitCallLog = repository.submitCallLog,
                result = AppResult.Success(
                    ProfileImageUpload(
                        bytes = byteArrayOf(4, 5, 6),
                        fileName = "mypage.png",
                        mimeType = "image/png",
                    ),
                ),
            ),
        )
        advanceUntilIdle()

        viewModel.dispatch(ProfileEditIntent.ProfileImageSelected("content://picked/profile.png"))
        viewModel.dispatch(ProfileEditIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(
            listOf(
                "read:content://picked/profile.png",
                "upload:mypage.png",
            ),
            repository.submitCallLog,
        )
    }

    @Test
    fun `닉네임과 이미지를 함께 변경하면 업로드 후 닉네임 수정을 호출한다`() = runTest {
        val repository = MyPageProfileEditFakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
        )
        val viewModel = createMyPageProfileEditViewModel(
            repository = repository,
            profileImageReader = LoggingProfileImageReader(
                submitCallLog = repository.submitCallLog,
                result = AppResult.Success(
                    ProfileImageUpload(
                        bytes = byteArrayOf(4, 5, 6),
                        fileName = "mypage.png",
                        mimeType = "image/png",
                    ),
                ),
            ),
        )
        advanceUntilIdle()

        viewModel.dispatch(ProfileEditIntent.NicknameChanged("새닉네임"))
        viewModel.dispatch(ProfileEditIntent.ProfileImageSelected("content://picked/profile.png"))
        viewModel.dispatch(ProfileEditIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(
            listOf(
                "read:content://picked/profile.png",
                "upload:mypage.png",
                "update:새닉네임",
            ),
            repository.submitCallLog,
        )
    }

    @Test
    fun `닉네임이 공백만 달라져 trim 결과가 같으면 저장 버튼이 비활성화된다`() = runTest {
        val repository = MyPageProfileEditFakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
        )
        val viewModel = createMyPageProfileEditViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.dispatch(ProfileEditIntent.NicknameChanged("  수빈  "))
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isSubmitEnabled)
    }

    @Test
    fun `프로필 저장 실패 시 스낵바 effect를 보낸다`() = runTest {
        val repository = MyPageProfileEditFakeMemberRepository(
            myInfoResult = AppResult.Success(defaultMemberInfo()),
            updateProfileResult = AppResult.Failure(AppError.NetworkUnavailable()),
        )
        val viewModel = createMyPageProfileEditViewModel(repository = repository)
        advanceUntilIdle()
        viewModel.dispatch(ProfileEditIntent.NicknameChanged("새닉네임"))
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(ProfileEditIntent.SubmitClicked)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is ProfileEditEffect.ShowSnackbar)
    }
}

private fun createMyPageProfileEditViewModel(
    repository: MyPageProfileEditFakeMemberRepository,
    profileImageReader: ProfileImageReader = LoggingProfileImageReader(repository.submitCallLog),
): MyPageProfileEditViewModel = MyPageProfileEditViewModel(
    getMyInfoUseCase = GetMyInfoUseCase(repository),
    updateMyProfileWithProfileImageUseCase = UpdateMyProfileWithProfileImageUseCase(
        uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
        updateProfileUseCase = UpdateProfileUseCase(repository),
    ),
    validateNicknameUseCase = ValidateNicknameUseCase(),
    profileImageReader = profileImageReader,
)

private fun defaultMemberInfo(): MemberInfo = MemberInfo(
    memberId = "member-1",
    nickname = "수빈",
    profileImageUrl = "https://example.com/profile.png",
    avatarColorHex = "#6C757F",
    role = MemberRole.MEMBER,
    status = MemberStatus.ACTIVE,
)

private class MyPageProfileEditFakeMemberRepository(
    private val myInfoResult: AppResult<MemberInfo>,
    private val updateProfileResult: AppResult<Unit> = AppResult.Success(Unit),
    private val uploadProfileImageResult: AppResult<Unit> = AppResult.Success(Unit),
) : MemberRepository {
    val submitCallLog: MutableList<String> = mutableListOf()

    override suspend fun getMyInfo(): AppResult<MemberInfo> = myInfoResult

    override suspend fun getOnboardingInfo(): AppResult<OnboardingInfo> {
        throw UnsupportedOperationException()
    }

    override suspend fun completeOnboarding(nickname: String): AppResult<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun updateProfile(nickname: String): AppResult<Unit> {
        submitCallLog += "update:$nickname"
        return updateProfileResult
    }

    override suspend fun changeState(
        status: MemberStatus?,
        role: MemberRole?,
    ): AppResult<MemberInfo> {
        throw UnsupportedOperationException()
    }

    override suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit> {
        submitCallLog += "upload:${image.fileName}"
        return uploadProfileImageResult
    }

    override suspend fun logout(): AppResult<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun withdraw(reason: String): AppResult<Unit> {
        throw UnsupportedOperationException()
    }
}

private class LoggingProfileImageReader(
    private val submitCallLog: MutableList<String>,
    private val result: AppResult<ProfileImageUpload> = AppResult.Success(
        ProfileImageUpload(
            bytes = byteArrayOf(1, 2, 3),
            fileName = "profile.png",
            mimeType = "image/png",
        ),
    ),
) : ProfileImageReader {
    override suspend fun read(imageUri: String): AppResult<ProfileImageUpload> {
        submitCallLog += "read:$imageUri"
        return result
    }
}
