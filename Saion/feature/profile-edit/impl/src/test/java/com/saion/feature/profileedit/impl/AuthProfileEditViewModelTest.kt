package com.saion.feature.profileedit.impl

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.domain.usecase.member.CompleteOnboardingUseCase
import com.saion.core.domain.usecase.member.CompleteOnboardingWithProfileImageUseCase
import com.saion.core.domain.usecase.member.GetOnboardingInfoUseCase
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
class AuthProfileEditViewModelTest {
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
    fun `온보딩 정보 조회 성공 시 초기 프로필 상태가 반영된다`() = runTest {
        val repository = AuthFakeMemberRepository(
            onboardingInfoResult = AppResult.Success(
                OnboardingInfo(
                    socialNickname = "사이온",
                    socialProfileImageUrl = "https://example.com/social.png",
                    avatarColorHex = "#FFD35C",
                ),
            ),
        )

        val viewModel = createAuthProfileEditViewModel(repository = repository)

        advanceUntilIdle()

        assertEquals(
            ProfileEditUiState(
                nicknamePlaceholder = "사이온",
                nickname = "사이온",
                validation = ValidateNicknameUseCase()("사이온"),
                profileImageUrl = "https://example.com/social.png",
                avatarColorHex = "#FFD35C",
                isSubmitting = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `이미지를 선택하면 로컬 프로필 이미지 상태가 반영된다`() = runTest {
        val repository = AuthFakeMemberRepository(
            onboardingInfoResult = AppResult.Success(
                OnboardingInfo(
                    socialNickname = "사이온",
                    socialProfileImageUrl = null,
                    avatarColorHex = "#E5E7EB",
                ),
            ),
        )
        val viewModel = createAuthProfileEditViewModel(repository = repository)

        advanceUntilIdle()
        viewModel.dispatch(ProfileEditIntent.ProfileImageSelected("content://picked/profile.png"))
        advanceUntilIdle()

        assertEquals("content://picked/profile.png", viewModel.uiState.value.selectedProfileImageUri)
    }

    @Test
    fun `유효하지 않은 닉네임은 제출하지 않는다`() = runTest {
        val repository = AuthFakeMemberRepository(
            onboardingInfoResult = AppResult.Success(
                OnboardingInfo(
                    socialNickname = null,
                    socialProfileImageUrl = null,
                    avatarColorHex = "#E5E7EB",
                ),
            ),
        )
        val viewModel = createAuthProfileEditViewModel(repository = repository)

        advanceUntilIdle()
        viewModel.dispatch(ProfileEditIntent.NicknameChanged("a"))
        viewModel.dispatch(ProfileEditIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(0, repository.completeOnboardingCalls)
        assertTrue(viewModel.uiState.value.validation?.isValid == false)
    }

    @Test
    fun `온보딩 완료 성공 시 완료 effect를 보낸다`() = runTest {
        val repository = AuthFakeMemberRepository(
            onboardingInfoResult = AppResult.Success(
                OnboardingInfo(
                    socialNickname = "사이온",
                    socialProfileImageUrl = null,
                    avatarColorHex = "#E5E7EB",
                ),
            ),
        )
        val viewModel = createAuthProfileEditViewModel(repository = repository)
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(ProfileEditIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(ProfileEditEffect.NavigateComplete, effectDeferred.await())
        assertEquals(listOf("complete:사이온"), repository.submitCallLog)
    }

    @Test
    fun `이미지를 선택한 뒤 온보딩 제출 시 업로드 후 완료를 호출한다`() = runTest {
        val repository = AuthFakeMemberRepository(
            onboardingInfoResult = AppResult.Success(
                OnboardingInfo(
                    socialNickname = "사이온",
                    socialProfileImageUrl = null,
                    avatarColorHex = "#E5E7EB",
                ),
            ),
        )
        val profileImage = ProfileImageUpload(
            bytes = byteArrayOf(7, 8, 9),
            fileName = "picked.png",
            mimeType = "image/png",
        )
        val viewModel = createAuthProfileEditViewModel(
            repository = repository,
            profileImageReader = FakeProfileImageReader(
                submitCallLog = repository.submitCallLog,
                result = AppResult.Success(profileImage),
            ),
        )
        advanceUntilIdle()

        viewModel.dispatch(ProfileEditIntent.ProfileImageSelected("content://picked/profile.png"))
        viewModel.dispatch(ProfileEditIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(
            listOf(
                "read:content://picked/profile.png",
                "upload:picked.png",
                "complete:사이온",
            ),
            repository.submitCallLog,
        )
    }

    @Test
    fun `이미지 읽기 실패 시 스낵바 effect를 보낸다`() = runTest {
        val repository = AuthFakeMemberRepository(
            onboardingInfoResult = AppResult.Success(
                OnboardingInfo(
                    socialNickname = "사이온",
                    socialProfileImageUrl = null,
                    avatarColorHex = "#E5E7EB",
                ),
            ),
        )
        val viewModel = createAuthProfileEditViewModel(
            repository = repository,
            profileImageReader = FakeProfileImageReader(
                submitCallLog = repository.submitCallLog,
                result = AppResult.Failure(
                    AppError.Unknown(message = PROFILE_IMAGE_READ_ERROR_MESSAGE),
                ),
            ),
        )
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(ProfileEditIntent.ProfileImageSelected("content://picked/profile.png"))
        viewModel.dispatch(ProfileEditIntent.SubmitClicked)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is ProfileEditEffect.ShowSnackbar)
        assertEquals(listOf("read:content://picked/profile.png"), repository.submitCallLog)
    }
}

private fun createAuthProfileEditViewModel(
    repository: AuthFakeMemberRepository,
    profileImageReader: ProfileImageReader = FakeProfileImageReader(),
): AuthProfileEditViewModel = AuthProfileEditViewModel(
    getOnboardingInfoUseCase = GetOnboardingInfoUseCase(repository),
    completeOnboardingWithProfileImageUseCase = CompleteOnboardingWithProfileImageUseCase(
        uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
        completeOnboardingUseCase = CompleteOnboardingUseCase(repository),
    ),
    validateNicknameUseCase = ValidateNicknameUseCase(),
    profileImageReader = profileImageReader,
)

private class AuthFakeMemberRepository(
    private val onboardingInfoResult: AppResult<OnboardingInfo>,
    private val completeOnboardingResult: AppResult<Unit> = AppResult.Success(Unit),
    private val uploadProfileImageResult: AppResult<Unit> = AppResult.Success(Unit),
) : MemberRepository {
    var completeOnboardingCalls: Int = 0
        private set
    val submitCallLog: MutableList<String> = mutableListOf()

    override suspend fun getMyInfo(): AppResult<MemberInfo> = AppResult.Success(
        MemberInfo(
            nickname = "unused",
            profileImageUrl = null,
            avatarColorHex = "#000000",
            role = MemberRole.MEMBER,
            status = MemberStatus.ACTIVE,
        ),
    )

    override suspend fun getOnboardingInfo(): AppResult<OnboardingInfo> = onboardingInfoResult

    override suspend fun completeOnboarding(nickname: String): AppResult<Unit> {
        completeOnboardingCalls += 1
        submitCallLog += "complete:$nickname"
        return completeOnboardingResult
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

private class FakeProfileImageReader(
    private val submitCallLog: MutableList<String>? = null,
    private val result: AppResult<ProfileImageUpload> = AppResult.Success(
        ProfileImageUpload(
            bytes = byteArrayOf(1, 2, 3),
            fileName = "profile.png",
            mimeType = "image/png",
        ),
    ),
) : ProfileImageReader {
    override suspend fun read(imageUri: String): AppResult<ProfileImageUpload> {
        submitCallLog?.add("read:$imageUri")
        return result
    }
}
