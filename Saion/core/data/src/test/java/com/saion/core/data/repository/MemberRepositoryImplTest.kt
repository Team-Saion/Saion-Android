package com.saion.core.data.repository

import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.datastore.datasource.CurrentCircleLocalDataSource
import com.saion.core.datastore.datasource.MemberProfileLocalDataSource
import com.saion.core.datastore.model.MemberProfileCache
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.MemberRemoteDataSource
import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.member.AvatarColorResponse
import com.saion.core.network.model.member.MemberInfoResponse
import com.saion.core.network.model.member.OnboardingInfoResponse
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MemberRepositoryImplTest {
    @Test
    fun `첫 getMyInfo는 원격 조회 후 캐시에 저장한다`() = runBlocking {
        val profileLocalDataSource = MemberTestProfileLocalDataSource()
        val remoteDataSource = FakeMemberRemoteDataSource(
            myInfoResponse = memberInfoResponse(
                nickname = "수빈",
                profileImageUrl = "http://example.com/profile.png",
                avatarColorHex = "#6C757F",
                role = "MEMBER",
                status = "ACTIVE",
            ),
        )
        val repository = createRepository(
            profileLocalDataSource = profileLocalDataSource,
            remoteDataSource = remoteDataSource,
        )

        val result = repository.getMyInfo()

        assertEquals(
            AppResult.Success(
                MemberInfo(
                    nickname = "수빈",
                    profileImageUrl = "https://example.com/profile.png",
                    avatarColorHex = "#6C757F",
                    role = MemberRole.MEMBER,
                    status = MemberStatus.ACTIVE,
                ),
            ),
            result,
        )
        assertEquals(1, remoteDataSource.getMyInfoCallCount)
        assertEquals(
            MemberProfileCache(
                hasValue = true,
                nickname = "수빈",
                profileImageUrl = "https://example.com/profile.png",
                avatarColorHex = "#6C757F",
                role = "MEMBER",
                status = "ACTIVE",
            ),
            profileLocalDataSource.savedProfile,
        )
    }

    @Test
    fun `같은 앱 실행의 두번째 getMyInfo는 캐시를 사용한다`() = runBlocking {
        val remoteDataSource = FakeMemberRemoteDataSource(
            myInfoResponse = memberInfoResponse(nickname = "첫응답"),
        )
        val repository = createRepository(remoteDataSource = remoteDataSource)

        repository.getMyInfo()
        val result = repository.getMyInfo()

        assertEquals(
            AppResult.Success(
                MemberInfo(
                    nickname = "첫응답",
                    profileImageUrl = null,
                    avatarColorHex = "#FFFFFF",
                    role = MemberRole.MEMBER,
                    status = MemberStatus.ACTIVE,
                ),
            ),
            result,
        )
        assertEquals(1, remoteDataSource.getMyInfoCallCount)
    }

    @Test
    fun `첫 원격 조회 실패 시 캐시 fallback 없이 실패를 반환한다`() = runBlocking {
        val profileLocalDataSource = MemberTestProfileLocalDataSource(
            savedProfile = MemberProfileCache(
                hasValue = true,
                nickname = "로컬닉네임",
                profileImageUrl = "",
                avatarColorHex = "#111111",
                role = "MEMBER",
                status = "ACTIVE",
            ),
        )
        val remoteDataSource = FakeMemberRemoteDataSource(
            myInfoResult = AppResult.Failure(AppError.NetworkUnavailable()),
        )
        val repository = createRepository(
            profileLocalDataSource = profileLocalDataSource,
            remoteDataSource = remoteDataSource,
        )

        val result = repository.getMyInfo()

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.NetworkUnavailable)
        assertEquals(1, remoteDataSource.getMyInfoCallCount)
    }

    @Test
    fun `첫 원격 조회 실패 후 다음 getMyInfo도 다시 원격을 시도한다`() = runBlocking {
        val remoteDataSource = FakeMemberRemoteDataSource(
            myInfoResults = listOf(
                AppResult.Failure(AppError.NetworkUnavailable()),
                AppResult.Success(memberInfoResponse(nickname = "성공닉네임")),
            ),
        )
        val repository = createRepository(remoteDataSource = remoteDataSource)

        val first = repository.getMyInfo()
        val second = repository.getMyInfo()

        assertTrue(first is AppResult.Failure)
        assertTrue((first as AppResult.Failure).error is AppError.NetworkUnavailable)
        assertEquals(2, remoteDataSource.getMyInfoCallCount)
        assertEquals(
            AppResult.Success(
                MemberInfo(
                    nickname = "성공닉네임",
                    profileImageUrl = null,
                    avatarColorHex = "#FFFFFF",
                    role = MemberRole.MEMBER,
                    status = MemberStatus.ACTIVE,
                ),
            ),
            second,
        )
    }

    @Test
    fun `updateProfile 성공 시 프로필 캐시를 최신 응답으로 갱신한다`() = runBlocking {
        val profileLocalDataSource = MemberTestProfileLocalDataSource()
        val repository = createRepository(
            profileLocalDataSource = profileLocalDataSource,
            remoteDataSource = FakeMemberRemoteDataSource(
                updateProfileResponse = memberInfoResponse(
                    nickname = "바뀐닉네임",
                    profileImageUrl = "http://example.com/updated.png",
                    avatarColorHex = "#222222",
                ),
            ),
        )

        val result = repository.updateProfile("바뀐닉네임")

        assertEquals(AppResult.Success(Unit), result)
        assertEquals("바뀐닉네임", profileLocalDataSource.savedProfile?.nickname)
        assertEquals("https://example.com/updated.png", profileLocalDataSource.savedProfile?.profileImageUrl)
    }

    @Test
    fun `logout 시 프로필 캐시를 비우고 다음 getMyInfo는 다시 원격을 시도한다`() = runBlocking {
        val profileLocalDataSource = MemberTestProfileLocalDataSource()
        val remoteDataSource = FakeMemberRemoteDataSource()
        val repository = createRepository(
            profileLocalDataSource = profileLocalDataSource,
            remoteDataSource = remoteDataSource,
        )

        repository.getMyInfo()
        repository.logout()
        repository.getMyInfo()

        assertEquals(2, remoteDataSource.getMyInfoCallCount)
        assertEquals(1, profileLocalDataSource.clearProfileCallCount)
        assertTrue(profileLocalDataSource.savedProfile != null)
    }
}

private fun createRepository(
    profileLocalDataSource: MemberTestProfileLocalDataSource = MemberTestProfileLocalDataSource(),
    remoteDataSource: FakeMemberRemoteDataSource = FakeMemberRemoteDataSource(),
): MemberRepositoryImpl = MemberRepositoryImpl(
    authLocalDataSource = MemberTestAuthLocalDataSource(),
    currentCircleLocalDataSource = MemberTestCurrentCircleLocalDataSource(),
    memberProfileLocalDataSource = profileLocalDataSource,
    memberRemoteDataSource = remoteDataSource,
)

private fun memberInfoResponse(
    nickname: String = "사이온",
    profileImageUrl: String? = null,
    avatarColorHex: String = "#FFFFFF",
    role: String = "MEMBER",
    status: String = "ACTIVE",
): MemberInfoResponse = MemberInfoResponse(
    id = "member-1",
    email = "saion@example.com",
    name = "Saion",
    nickname = nickname,
    role = role,
    avatarColor = AvatarColorResponse(
        code = "custom",
        hex = avatarColorHex,
    ),
    profileImageKey = "profile-key",
    profileImageUrl = profileImageUrl,
    status = status,
    createdAt = "2026-07-17T00:00:00",
)

private class FakeMemberRemoteDataSource(
    private val myInfoResponse: MemberInfoResponse = memberInfoResponse(),
    private val myInfoResult: AppResult<MemberInfoResponse>? = null,
    private val myInfoResults: List<AppResult<MemberInfoResponse>> = emptyList(),
    private val updateProfileResponse: MemberInfoResponse = memberInfoResponse(),
) : MemberRemoteDataSource {
    var getMyInfoCallCount: Int = 0
        private set

    override suspend fun getMyInfo(): ApiResponse<MemberInfoResponse> {
        val result = myInfoResults.getOrNull(getMyInfoCallCount) ?: myInfoResult ?: AppResult.Success(myInfoResponse)
        getMyInfoCallCount += 1
        return result.toMemberInfoApiResponse()
    }

    override suspend fun getOnboardingInfo(): ApiResponse<OnboardingInfoResponse> {
        throw UnsupportedOperationException()
    }

    override suspend fun completeOnboarding(nickname: String): ApiResponse<TokenResponse> {
        throw UnsupportedOperationException()
    }

    override suspend fun updateProfile(nickname: String): ApiResponse<MemberInfoResponse> =
        AppResult.Success(updateProfileResponse).toMemberInfoApiResponse()

    override suspend fun changeState(
        status: String?,
        role: String?,
    ): ApiResponse<MemberInfoResponse> {
        throw UnsupportedOperationException()
    }

    override suspend fun uploadProfileImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): ApiResponse<MemberInfoResponse> {
        throw UnsupportedOperationException()
    }

    override suspend fun logout(): ApiResponse<Unit> = ApiResponse(
        statusCode = 200,
        isSuccess = true,
        data = Unit,
        errorCode = null,
        message = null,
        timestamp = "2026-07-17T00:00:00",
    )

    override suspend fun withdraw(reason: String): ApiResponse<Unit> {
        throw UnsupportedOperationException()
    }
}

private fun AppResult<MemberInfoResponse>.toMemberInfoApiResponse(): ApiResponse<MemberInfoResponse> = when (this) {
    is AppResult.Success -> ApiResponse(
        statusCode = 200,
        isSuccess = true,
        data = data,
        errorCode = null,
        message = null,
        timestamp = "2026-07-17T00:00:00",
    )

    is AppResult.Failure -> throw when (error) {
        is AppError.NetworkUnavailable -> IOException()
        else -> IllegalStateException("Unsupported test error: $error")
    }
}

private class MemberTestAuthLocalDataSource : AuthLocalDataSource {
    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) = Unit

    override suspend fun getAccessToken(): String? = null

    override suspend fun getRefreshToken(): String? = null

    override suspend fun hasSession(): Boolean = false

    override suspend fun clearTokens() = Unit
}

private class MemberTestCurrentCircleLocalDataSource : CurrentCircleLocalDataSource {
    override fun observeSelectedCircleId(): Flow<String?> = flowOf(null)

    override suspend fun getSelectedCircleId(): String? = null

    override suspend fun saveSelectedCircleId(circleId: String?) = Unit

    override suspend fun clearSelectedCircleId() = Unit
}

private class MemberTestProfileLocalDataSource(
    var savedProfile: MemberProfileCache? = null,
) : MemberProfileLocalDataSource {
    var clearProfileCallCount: Int = 0
        private set

    override suspend fun getProfile(): MemberProfileCache? = savedProfile

    override suspend fun saveProfile(profile: MemberProfileCache) {
        savedProfile = profile
    }

    override suspend fun clearProfile() {
        clearProfileCallCount += 1
        savedProfile = null
    }
}
