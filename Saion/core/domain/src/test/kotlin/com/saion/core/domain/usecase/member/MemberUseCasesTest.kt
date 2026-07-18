package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MemberUseCasesTest {
    @Test
    fun `멤버 상태 변경은 status와 role을 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(
            MemberInfo(
                nickname = "관리자",
                profileImageUrl = null,
                avatarColorHex = "#FFFFFF",
                role = MemberRole.ADMIN,
                status = MemberStatus.DELETED,
            ),
        )
        val repository = FakeMemberRepository(changeStateResult = expected)

        val actual = ChangeStateUseCase(repository).invoke(
            status = MemberStatus.DELETED,
            role = MemberRole.ADMIN,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = expected,
                calls = listOf(
                    MemberRepositoryCall.ChangeState(
                        status = MemberStatus.DELETED,
                        role = MemberRole.ADMIN,
                    ),
                ),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `회원 탈퇴는 사유를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(Unit)
        val repository = FakeMemberRepository(withdrawResult = expected)

        val actual = WithdrawUseCase(repository).invoke(reason = "테스트 종료")

        assertEquals(
            MemberUseCaseOutcome(
                result = expected,
                calls = listOf(MemberRepositoryCall.Withdraw(reason = "테스트 종료")),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `온보딩 합성 유즈케이스는 이미지가 있으면 업로드 후 완료를 호출한다`() = runBlocking {
        val repository = FakeMemberRepository()
        val profileImage = sampleProfileImageUpload()
        val useCase = CompleteOnboardingWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            completeOnboardingUseCase = CompleteOnboardingUseCase(repository),
        )

        val actual = useCase(
            nickname = "사이온",
            profileImage = profileImage,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = AppResult.Success(Unit),
                calls = listOf(
                    MemberRepositoryCall.UploadProfileImage(profileImage),
                    MemberRepositoryCall.CompleteOnboarding("사이온"),
                ),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `온보딩 합성 유즈케이스는 이미지가 없으면 완료만 호출한다`() = runBlocking {
        val repository = FakeMemberRepository()
        val useCase = CompleteOnboardingWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            completeOnboardingUseCase = CompleteOnboardingUseCase(repository),
        )

        val actual = useCase(
            nickname = "사이온",
            profileImage = null,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = AppResult.Success(Unit),
                calls = listOf(MemberRepositoryCall.CompleteOnboarding("사이온")),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `온보딩 합성 유즈케이스는 이미지 업로드가 실패하면 완료를 호출하지 않는다`() = runBlocking {
        val failure = AppResult.Failure(AppError.NetworkUnavailable())
        val repository = FakeMemberRepository(uploadProfileImageResult = failure)
        val profileImage = sampleProfileImageUpload()
        val useCase = CompleteOnboardingWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            completeOnboardingUseCase = CompleteOnboardingUseCase(repository),
        )

        val actual = useCase(
            nickname = "사이온",
            profileImage = profileImage,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = failure,
                calls = listOf(MemberRepositoryCall.UploadProfileImage(profileImage)),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `온보딩 합성 유즈케이스는 완료가 실패하면 그 실패를 반환한다`() = runBlocking {
        val failure = AppResult.Failure(AppError.Timeout())
        val repository = FakeMemberRepository(completeOnboardingResult = failure)
        val profileImage = sampleProfileImageUpload()
        val useCase = CompleteOnboardingWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            completeOnboardingUseCase = CompleteOnboardingUseCase(repository),
        )

        val actual = useCase(
            nickname = "사이온",
            profileImage = profileImage,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = failure,
                calls = listOf(
                    MemberRepositoryCall.UploadProfileImage(profileImage),
                    MemberRepositoryCall.CompleteOnboarding("사이온"),
                ),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `프로필 수정 합성 유즈케이스는 이미지가 있으면 업로드 후 수정을 호출한다`() = runBlocking {
        val repository = FakeMemberRepository()
        val profileImage = sampleProfileImageUpload()
        val useCase = UpdateMyProfileWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            updateProfileUseCase = UpdateProfileUseCase(repository),
        )

        val actual = useCase(
            nickname = "새닉네임",
            profileImage = profileImage,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = AppResult.Success(Unit),
                calls = listOf(
                    MemberRepositoryCall.UploadProfileImage(profileImage),
                    MemberRepositoryCall.UpdateProfile("새닉네임"),
                ),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `프로필 수정 합성 유즈케이스는 이미지가 없으면 수정만 호출한다`() = runBlocking {
        val repository = FakeMemberRepository()
        val useCase = UpdateMyProfileWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            updateProfileUseCase = UpdateProfileUseCase(repository),
        )

        val actual = useCase(
            nickname = "새닉네임",
            profileImage = null,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = AppResult.Success(Unit),
                calls = listOf(MemberRepositoryCall.UpdateProfile("새닉네임")),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `프로필 수정 합성 유즈케이스는 이미지 업로드가 실패하면 수정을 호출하지 않는다`() = runBlocking {
        val failure = AppResult.Failure(AppError.NetworkUnavailable())
        val repository = FakeMemberRepository(uploadProfileImageResult = failure)
        val profileImage = sampleProfileImageUpload()
        val useCase = UpdateMyProfileWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            updateProfileUseCase = UpdateProfileUseCase(repository),
        )

        val actual = useCase(
            nickname = "새닉네임",
            profileImage = profileImage,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = failure,
                calls = listOf(MemberRepositoryCall.UploadProfileImage(profileImage)),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `프로필 수정 합성 유즈케이스는 이미지만 변경되면 업로드만 호출한다`() = runBlocking {
        val repository = FakeMemberRepository()
        val profileImage = sampleProfileImageUpload()
        val useCase = UpdateMyProfileWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            updateProfileUseCase = UpdateProfileUseCase(repository),
        )

        val actual = useCase(
            nickname = null,
            profileImage = profileImage,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = AppResult.Success(Unit),
                calls = listOf(MemberRepositoryCall.UploadProfileImage(profileImage)),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `프로필 수정 합성 유즈케이스는 변경사항이 없으면 아무것도 호출하지 않는다`() = runBlocking {
        val repository = FakeMemberRepository()
        val useCase = UpdateMyProfileWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            updateProfileUseCase = UpdateProfileUseCase(repository),
        )

        val actual = useCase(
            nickname = null,
            profileImage = null,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = AppResult.Success(Unit),
                calls = emptyList(),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }

    @Test
    fun `프로필 수정 합성 유즈케이스는 수정이 실패하면 그 실패를 반환한다`() = runBlocking {
        val failure = AppResult.Failure(AppError.ServerUnavailable())
        val repository = FakeMemberRepository(updateProfileResult = failure)
        val profileImage = sampleProfileImageUpload()
        val useCase = UpdateMyProfileWithProfileImageUseCase(
            uploadProfileImageUseCase = UploadProfileImageUseCase(repository),
            updateProfileUseCase = UpdateProfileUseCase(repository),
        )

        val actual = useCase(
            nickname = "새닉네임",
            profileImage = profileImage,
        )

        assertEquals(
            MemberUseCaseOutcome(
                result = failure,
                calls = listOf(
                    MemberRepositoryCall.UploadProfileImage(profileImage),
                    MemberRepositoryCall.UpdateProfile("새닉네임"),
                ),
            ),
            MemberUseCaseOutcome(
                result = actual,
                calls = repository.calls,
            ),
        )
    }
}

private data class MemberUseCaseOutcome<T>(
    val result: AppResult<T>,
    val calls: List<MemberRepositoryCall>,
)

private sealed interface MemberRepositoryCall {
    data class CompleteOnboarding(val nickname: String) : MemberRepositoryCall

    data class UpdateProfile(val nickname: String) : MemberRepositoryCall

    data class ChangeState(
        val status: MemberStatus?,
        val role: MemberRole?,
    ) : MemberRepositoryCall

    data class UploadProfileImage(val image: ProfileImageUpload) : MemberRepositoryCall

    data class Withdraw(val reason: String) : MemberRepositoryCall
}

private class FakeMemberRepository(
    private val myInfoResult: AppResult<MemberInfo> = AppResult.Success(
        MemberInfo(
            nickname = "사이온",
            profileImageUrl = null,
            avatarColorHex = "#FFFFFF",
            role = MemberRole.MEMBER,
            status = MemberStatus.ACTIVE,
        ),
    ),
    private val onboardingInfoResult: AppResult<OnboardingInfo> = AppResult.Success(
        OnboardingInfo(
            socialNickname = "saion",
            socialProfileImageUrl = null,
            avatarColorHex = "#FFFFFF",
        ),
    ),
    private val completeOnboardingResult: AppResult<Unit> = AppResult.Success(Unit),
    private val updateProfileResult: AppResult<Unit> = AppResult.Success(Unit),
    private val changeStateResult: AppResult<MemberInfo> = AppResult.Success(
        MemberInfo(
            nickname = "사이온",
            profileImageUrl = null,
            avatarColorHex = "#FFFFFF",
            role = MemberRole.MEMBER,
            status = MemberStatus.ACTIVE,
        ),
    ),
    private val uploadProfileImageResult: AppResult<Unit> = AppResult.Success(Unit),
    private val logoutResult: AppResult<Unit> = AppResult.Success(Unit),
    private val withdrawResult: AppResult<Unit> = AppResult.Success(Unit),
) : MemberRepository {
    val calls: MutableList<MemberRepositoryCall> = mutableListOf()

    override suspend fun getMyInfo(): AppResult<MemberInfo> = myInfoResult

    override suspend fun getOnboardingInfo(): AppResult<OnboardingInfo> = onboardingInfoResult

    override suspend fun completeOnboarding(nickname: String): AppResult<Unit> {
        calls += MemberRepositoryCall.CompleteOnboarding(nickname)
        return completeOnboardingResult
    }

    override suspend fun updateProfile(nickname: String): AppResult<Unit> {
        calls += MemberRepositoryCall.UpdateProfile(nickname)
        return updateProfileResult
    }

    override suspend fun changeState(
        status: MemberStatus?,
        role: MemberRole?,
    ): AppResult<MemberInfo> {
        calls += MemberRepositoryCall.ChangeState(status = status, role = role)
        return changeStateResult
    }

    override suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit> {
        calls += MemberRepositoryCall.UploadProfileImage(image)
        return uploadProfileImageResult
    }

    override suspend fun logout(): AppResult<Unit> = logoutResult

    override suspend fun withdraw(reason: String): AppResult<Unit> {
        calls += MemberRepositoryCall.Withdraw(reason = reason)
        return withdrawResult
    }
}

private fun sampleProfileImageUpload(): ProfileImageUpload = ProfileImageUpload(
    bytes = byteArrayOf(1, 2, 3),
    fileName = "profile.png",
    mimeType = "image/png",
)
