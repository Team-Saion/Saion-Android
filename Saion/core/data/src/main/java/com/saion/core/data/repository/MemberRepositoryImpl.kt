package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.model.member.MemberInfoResponse
import com.saion.core.network.datasource.MemberRemoteDataSource
import javax.inject.Inject

/**
 * 멤버 원격 응답을 도메인 결과로 변환하는 기본 구현입니다.
 */
internal class MemberRepositoryImpl @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
    private val memberRemoteDataSource: MemberRemoteDataSource,
) : MemberRepository {
    override suspend fun getMyInfo(): AppResult<MemberInfo> = safeRequest(
        request = { memberRemoteDataSource.getMyInfo() },
    ) { response -> response.toMemberInfoResult() }

    override suspend fun getOnboardingInfo(): AppResult<OnboardingInfo> = safeRequest(
        request = { memberRemoteDataSource.getOnboardingInfo() },
    ) { response ->
        AppResult.Success(
            OnboardingInfo(
                socialNickname = response.socialNickname,
                socialProfileImageUrl = response.socialProfileImageUrl?.normalizeProfileImageUrl(),
                avatarColorHex = response.avatarColor.hex,
            ),
        )
    }

    override suspend fun completeOnboarding(nickname: String): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.completeOnboarding(nickname = nickname) },
    ) { response ->
        authLocalDataSource.saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )
        AppResult.Success(Unit)
    }

    override suspend fun updateProfile(nickname: String): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.updateProfile(nickname = nickname) },
    ) {
        AppResult.Success(Unit)
    }

    override suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit> = safeRequest(
        request = {
            memberRemoteDataSource.uploadProfileImage(
                imageBytes = image.bytes,
                fileName = image.fileName,
                mimeType = image.mimeType,
            )
        },
    ) {
        AppResult.Success(Unit)
    }

    override suspend fun logout(): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.logout() },
    ) {
        authLocalDataSource.clearTokens()
        AppResult.Success(Unit)
    }

    override suspend fun withdraw(): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.withdraw() },
    ) {
        authLocalDataSource.clearTokens()
        AppResult.Success(Unit)
    }
}

private fun MemberInfoResponse.toMemberInfoResult(): AppResult<MemberInfo> {
    val memberRole = MemberRole.from(role)
        ?: return AppResult.Failure(
            AppError.Unknown(message = "Member role is missing or invalid."),
        )
    val memberStatus = MemberStatus.from(status)
        ?: return AppResult.Failure(
            AppError.Unknown(message = "Member status is missing or invalid."),
        )

    return AppResult.Success(
        MemberInfo(
            role = memberRole,
            status = memberStatus,
        ),
    )
}

private fun String.normalizeProfileImageUrl(): String = replace(
    oldValue = "http://",
    newValue = "https://",
).replace(
    oldValue = "http%3A%2F%2F",
    newValue = "https%3A%2F%2F",
    ignoreCase = true,
)
