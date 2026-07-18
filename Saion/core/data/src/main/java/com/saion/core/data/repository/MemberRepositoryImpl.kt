package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.datastore.datasource.CircleLocalDataSource
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 멤버 원격 응답을 도메인 결과로 변환하는 기본 구현입니다.
 */
internal class MemberRepositoryImpl @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
    private val circleLocalDataSource: CircleLocalDataSource,
    private val currentCircleLocalDataSource: CurrentCircleLocalDataSource,
    private val memberProfileLocalDataSource: MemberProfileLocalDataSource,
    private val memberRemoteDataSource: MemberRemoteDataSource,
) : MemberRepository {
    private val myInfoState = MutableStateFlow<MemberInfo?>(null)

    override fun observeMyInfo(): Flow<MemberInfo?> = myInfoState.asStateFlow()

    override suspend fun getMyInfo(): AppResult<MemberInfo> {
        myInfoState.value?.let { return AppResult.Success(it) }
        val cachedProfile = memberProfileLocalDataSource.getProfile()?.toMemberInfo()
        if (cachedProfile != null) {
            myInfoState.value = cachedProfile
            return AppResult.Success(cachedProfile)
        }
        return refreshMyInfo()
    }

    override suspend fun refreshMyInfo(): AppResult<MemberInfo> = fetchMyInfoFromRemote()

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
        AppResult.Success(Unit)
    }

    override suspend fun logout(): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.logout() },
    ) {
        authLocalDataSource.clearTokens()
        circleLocalDataSource.clearCircles()
        currentCircleLocalDataSource.clearSelectedCircleId()
        memberProfileLocalDataSource.clearProfile()
        myInfoState.value = null
        AppResult.Success(Unit)
    }

    override suspend fun withdraw(reason: String): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.withdraw(reason = reason) },
    ) {
        authLocalDataSource.clearTokens()
        circleLocalDataSource.clearCircles()
        currentCircleLocalDataSource.clearSelectedCircleId()
        memberProfileLocalDataSource.clearProfile()
        myInfoState.value = null
        AppResult.Success(Unit)
    }

    private suspend fun fetchMyInfoFromRemote(): AppResult<MemberInfo> = safeRequest(
        request = { memberRemoteDataSource.getMyInfo() },
    ) { response ->
        val memberInfo = response.toMemberInfo()
        syncProfileCache(memberInfo)
        AppResult.Success(memberInfo)
    }

    private suspend fun syncProfileCache(memberInfo: MemberInfo) {
        memberProfileLocalDataSource.saveProfile(memberInfo.toCache())
        myInfoState.value = memberInfo
    }
}

private fun MemberInfoResponse.toMemberInfo(): MemberInfo {
    val memberRole = MemberRole.from(role)
        ?: throw IllegalArgumentException("Member role is missing or invalid.")
    val memberStatus = MemberStatus.from(status)
        ?: throw IllegalArgumentException("Member status is missing or invalid.")

    return MemberInfo(
        memberId = id,
        nickname = nickname,
        profileImageUrl = profileImageUrl?.normalizeProfileImageUrl(),
        avatarColorHex = avatarColor.hex,
        role = memberRole,
        status = memberStatus,
    )
}

private fun MemberProfileCache.toMemberInfo(): MemberInfo? {
    val memberRole = MemberRole.from(role)
        ?: return null
    val memberStatus = MemberStatus.from(status)
        ?: return null

    return MemberInfo(
        memberId = memberId,
        nickname = nickname,
        profileImageUrl = profileImageUrl.ifBlank { null }?.normalizeProfileImageUrl(),
        avatarColorHex = avatarColorHex,
        role = memberRole,
        status = memberStatus,
    )
}

private fun MemberInfo.toCache(): MemberProfileCache = MemberProfileCache(
    hasValue = true,
    memberId = memberId,
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
