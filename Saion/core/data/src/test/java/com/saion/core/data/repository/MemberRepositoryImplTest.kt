package com.saion.core.data.repository

import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.datastore.datasource.CurrentCircleLocalDataSource
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.MemberRemoteDataSource
import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.member.AvatarColorResponse
import com.saion.core.network.model.member.MemberInfoResponse
import com.saion.core.network.model.member.OnboardingInfoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MemberRepositoryImplTest {
    @Test
    fun `getMyInfo maps nickname profile image and avatar color`() = runBlocking {
        val repository = createRepository(
            remoteDataSource = FakeMemberRemoteDataSource(
                myInfoResponse = memberInfoResponse(
                    nickname = "수빈",
                    profileImageUrl = "http://example.com/profile.png",
                    avatarColorHex = "#6C757F",
                    role = "MEMBER",
                    status = "ACTIVE",
                ),
            ),
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
    }
}

private fun createRepository(
    remoteDataSource: FakeMemberRemoteDataSource = FakeMemberRemoteDataSource(),
): MemberRepositoryImpl = MemberRepositoryImpl(
    authLocalDataSource = MemberTestAuthLocalDataSource(),
    currentCircleLocalDataSource = MemberTestCurrentCircleLocalDataSource(),
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
) : MemberRemoteDataSource {
    override suspend fun getMyInfo(): ApiResponse<MemberInfoResponse> = ApiResponse(
        statusCode = 200,
        isSuccess = true,
        data = myInfoResponse,
        errorCode = null,
        message = null,
        timestamp = "2026-07-17T00:00:00",
    )

    override suspend fun getOnboardingInfo(): ApiResponse<OnboardingInfoResponse> {
        throw UnsupportedOperationException()
    }

    override suspend fun completeOnboarding(nickname: String): ApiResponse<TokenResponse> {
        throw UnsupportedOperationException()
    }

    override suspend fun updateProfile(nickname: String): ApiResponse<MemberInfoResponse> {
        throw UnsupportedOperationException()
    }

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

    override suspend fun logout(): ApiResponse<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun withdraw(reason: String): ApiResponse<Unit> {
        throw UnsupportedOperationException()
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
