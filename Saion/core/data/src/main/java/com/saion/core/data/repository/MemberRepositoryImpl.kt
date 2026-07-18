package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.datastore.datasource.CurrentCircleLocalDataSource
import com.saion.core.datastore.datasource.MemberProfileLocalDataSource
import com.saion.core.datastore.model.MemberProfileCache
import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.MemberRemoteDataSource
import com.saion.core.network.model.member.MemberInfoResponse
import javax.inject.Inject
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 멤버 원격 응답을 도메인 결과로 변환하는 기본 구현입니다.
 */
internal class MemberRepositoryImpl @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
    private val currentCircleLocalDataSource: CurrentCircleLocalDataSource,
    private val memberProfileLocalDataSource: MemberProfileLocalDataSource,
    private val memberRemoteDataSource: MemberRemoteDataSource,
) : MemberRepository {
    private val hasFetchedMyInfoThisRun = AtomicBoolean(false)

    override suspend fun getMyInfo(): AppResult<MemberInfo> {
        if (!hasFetchedMyInfoThisRun.get()) {
            return fetchMyInfoFromRemote()
        }

        val cachedProfile = memberProfileLocalDataSource.getProfile()
        return if (cachedProfile != null) {
            cachedProfile.toMemberInfoResult()
        } else {
            fetchMyInfoFromRemote()
        }
    }

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
    ) { response ->
        syncProfileCache(response.toMemberInfo())
        hasFetchedMyInfoThisRun.set(true)
        AppResult.Success(Unit)
    }

    override suspend fun changeState(
        status: MemberStatus?,
        role: MemberRole?,
    ): AppResult<MemberInfo> = safeRequest(
        request = {
            memberRemoteDataSource.changeState(
                status = status?.value,
                role = role?.value,
            )
        },
    ) { response ->
        val memberInfo = response.toMemberInfo()
        syncProfileCache(memberInfo)
        hasFetchedMyInfoThisRun.set(true)
        AppResult.Success(memberInfo)
    }

    override suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit> = safeRequest(
        request = {
            memberRemoteDataSource.uploadProfileImage(
                imageBytes = image.bytes,
                fileName = image.fileName,
                mimeType = image.mimeType,
            )
        },
    ) { response ->
        syncProfileCache(response.toMemberInfo())
        hasFetchedMyInfoThisRun.set(true)
        AppResult.Success(Unit)
    }

    override suspend fun logout(): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.logout() },
    ) {
        authLocalDataSource.clearTokens()
        currentCircleLocalDataSource.clearSelectedCircleId()
        memberProfileLocalDataSource.clearProfile()
        hasFetchedMyInfoThisRun.set(false)
        AppResult.Success(Unit)
    }

    override suspend fun withdraw(reason: String): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.withdraw(reason = reason) },
    ) {
        authLocalDataSource.clearTokens()
        currentCircleLocalDataSource.clearSelectedCircleId()
        memberProfileLocalDataSource.clearProfile()
        hasFetchedMyInfoThisRun.set(false)
        AppResult.Success(Unit)
    }

    private suspend fun fetchMyInfoFromRemote(): AppResult<MemberInfo> = safeRequest(
        request = { memberRemoteDataSource.getMyInfo() },
    ) { response ->
        val memberInfo = response.toMemberInfo()
        syncProfileCache(memberInfo)
        hasFetchedMyInfoThisRun.set(true)
        AppResult.Success(memberInfo)
    }

    private suspend fun syncProfileCache(memberInfo: MemberInfo) {
        memberProfileLocalDataSource.saveProfile(memberInfo.toCache())
    }
}

private fun MemberInfoResponse.toMemberInfoResult(): AppResult<MemberInfo> = toMemberInfo()
    .let { AppResult.Success(it) }

private fun MemberInfoResponse.toMemberInfo(): MemberInfo {
    val memberRole = MemberRole.from(role)
        ?: throw IllegalArgumentException("Member role is missing or invalid.")
    val memberStatus = MemberStatus.from(status)
        ?: throw IllegalArgumentException("Member status is missing or invalid.")

    return MemberInfo(
        nickname = nickname,
        profileImageUrl = profileImageUrl?.normalizeProfileImageUrl(),
        avatarColorHex = avatarColor.hex,
        role = memberRole,
        status = memberStatus,
    )
}

private fun MemberProfileCache.toMemberInfoResult(): AppResult<MemberInfo> {
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
            nickname = nickname,
            profileImageUrl = profileImageUrl.ifBlank { null }?.normalizeProfileImageUrl(),
            avatarColorHex = avatarColorHex,
            role = memberRole,
            status = memberStatus,
        ),
    )
}

private fun MemberInfo.toCache(): MemberProfileCache = MemberProfileCache(
    hasValue = true,
    nickname = nickname,
    profileImageUrl = profileImageUrl.orEmpty(),
    avatarColorHex = avatarColorHex,
    role = role.value,
    status = status.value,
)

private fun String.normalizeProfileImageUrl(): String = replace(
    oldValue = "http://",
    newValue = "https://",
).replace(
    oldValue = "http%3A%2F%2F",
    newValue = "https%3A%2F%2F",
    ignoreCase = true,
)
