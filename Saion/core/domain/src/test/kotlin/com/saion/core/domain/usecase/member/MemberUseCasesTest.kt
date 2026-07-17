package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MemberUseCasesTest {
    @Test
    fun `멤버 상태 변경은 status와 role을 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(
            MemberInfo(
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
                call = MemberRepositoryCall.ChangeState(
                    status = MemberStatus.DELETED,
                    role = MemberRole.ADMIN,
                ),
            ),
            MemberUseCaseOutcome(
                result = actual,
                call = repository.lastCall,
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
                call = MemberRepositoryCall.Withdraw(reason = "테스트 종료"),
            ),
            MemberUseCaseOutcome(
                result = actual,
                call = repository.lastCall,
            ),
        )
    }
}

private data class MemberUseCaseOutcome<T>(
    val result: AppResult<T>,
    val call: MemberRepositoryCall?,
)

private sealed interface MemberRepositoryCall {
    data class ChangeState(
        val status: MemberStatus?,
        val role: MemberRole?,
    ) : MemberRepositoryCall

    data class Withdraw(val reason: String) : MemberRepositoryCall
}

private class FakeMemberRepository(
    private val myInfoResult: AppResult<MemberInfo> = AppResult.Success(
        MemberInfo(
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
            role = MemberRole.MEMBER,
            status = MemberStatus.ACTIVE,
        ),
    ),
    private val uploadProfileImageResult: AppResult<Unit> = AppResult.Success(Unit),
    private val logoutResult: AppResult<Unit> = AppResult.Success(Unit),
    private val withdrawResult: AppResult<Unit> = AppResult.Success(Unit),
) : MemberRepository {
    var lastCall: MemberRepositoryCall? = null

    override suspend fun getMyInfo(): AppResult<MemberInfo> = myInfoResult

    override suspend fun getOnboardingInfo(): AppResult<OnboardingInfo> = onboardingInfoResult

    override suspend fun completeOnboarding(nickname: String): AppResult<Unit> = completeOnboardingResult

    override suspend fun updateProfile(nickname: String): AppResult<Unit> = updateProfileResult

    override suspend fun changeState(
        status: MemberStatus?,
        role: MemberRole?,
    ): AppResult<MemberInfo> {
        lastCall = MemberRepositoryCall.ChangeState(status = status, role = role)
        return changeStateResult
    }

    override suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit> = uploadProfileImageResult

    override suspend fun logout(): AppResult<Unit> = logoutResult

    override suspend fun withdraw(reason: String): AppResult<Unit> {
        lastCall = MemberRepositoryCall.Withdraw(reason = reason)
        return withdrawResult
    }
}
