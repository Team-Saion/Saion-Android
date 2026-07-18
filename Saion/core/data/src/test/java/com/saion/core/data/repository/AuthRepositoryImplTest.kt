package com.saion.core.data.repository

import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.datastore.datasource.CurrentCircleLocalDataSource
import com.saion.core.datastore.datasource.MemberProfileLocalDataSource
import com.saion.core.datastore.model.MemberProfileCache
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.AuthRemoteDataSource
import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse
import java.util.Base64
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {
    @Test
    fun `loginWithKakao returns pending role when access token contains pending claim`() = runBlocking {
        val repository = createRepository(accessToken = jwtWithRoles("PENDING"))

        val result = repository.loginWithKakao(idToken = "id-token")

        assertEquals(AppResult.Success(MemberRole.PENDING), result)
    }

    @Test
    fun `loginWithKakao returns member role when access token contains member claim`() = runBlocking {
        val repository = createRepository(accessToken = jwtWithRoles("MEMBER"))

        val result = repository.loginWithKakao(idToken = "id-token")

        assertEquals(AppResult.Success(MemberRole.MEMBER), result)
    }

    @Test
    fun `loginWithKakao returns admin role when access token contains admin claim`() = runBlocking {
        val repository = createRepository(accessToken = jwtWithRoles("ADMIN"))

        val result = repository.loginWithKakao(idToken = "id-token")

        assertEquals(AppResult.Success(MemberRole.ADMIN), result)
    }

    @Test
    fun `loginWithKakao returns failure when roles claim is missing`() = runBlocking {
        val repository = createRepository(accessToken = jwtWithPayload("{}"))

        val result = repository.loginWithKakao(idToken = "id-token")

        assertInvalidRoleFailure(result)
    }

    @Test
    fun `loginWithKakao returns failure when roles claim is blank`() = runBlocking {
        val repository = createRepository(accessToken = jwtWithRoles(""))

        val result = repository.loginWithKakao(idToken = "id-token")

        assertInvalidRoleFailure(result)
    }

    @Test
    fun `loginWithKakao returns failure when roles claim is empty`() = runBlocking {
        val repository = createRepository(accessToken = jwtWithPayload("""{"roles":[]}"""))

        val result = repository.loginWithKakao(idToken = "id-token")

        assertInvalidRoleFailure(result)
    }

    @Test
    fun `loginWithKakao returns failure when payload is not valid base64`() = runBlocking {
        val repository = createRepository(accessToken = "header.%%%.signature")

        val result = repository.loginWithKakao(idToken = "id-token")

        assertInvalidRoleFailure(result)
    }

    @Test
    fun `loginWithKakao returns failure when payload is not valid json`() = runBlocking {
        val repository = createRepository(accessToken = jwtWithPayload("{invalid-json"))

        val result = repository.loginWithKakao(idToken = "id-token")

        assertInvalidRoleFailure(result)
    }

    @Test
    fun `loginWithKakao returns failure when roles claim is unknown`() = runBlocking {
        val repository = createRepository(accessToken = jwtWithRoles("GUEST"))

        val result = repository.loginWithKakao(idToken = "id-token")

        assertInvalidRoleFailure(result)
    }

    @Test
    fun `getStoredMemberRole returns pending role when stored access token contains pending claim`() = runBlocking {
        val localDataSource = FakeAuthLocalDataSource(accessToken = jwtWithRoles("PENDING"))
        val repository = createRepository(localDataSource = localDataSource)

        val result = repository.getStoredMemberRole()

        assertEquals(AppResult.Success(MemberRole.PENDING), result)
    }

    @Test
    fun `getStoredMemberRole returns member role when stored access token contains member claim`() = runBlocking {
        val localDataSource = FakeAuthLocalDataSource(accessToken = jwtWithRoles("MEMBER"))
        val repository = createRepository(localDataSource = localDataSource)

        val result = repository.getStoredMemberRole()

        assertEquals(AppResult.Success(MemberRole.MEMBER), result)
    }

    @Test
    fun `getStoredMemberRole returns admin role when stored access token contains admin claim`() = runBlocking {
        val localDataSource = FakeAuthLocalDataSource(accessToken = jwtWithRoles("ADMIN"))
        val repository = createRepository(localDataSource = localDataSource)

        val result = repository.getStoredMemberRole()

        assertEquals(AppResult.Success(MemberRole.ADMIN), result)
    }

    @Test
    fun `getStoredMemberRole returns failure when stored access token is missing`() = runBlocking {
        val repository = createRepository(localDataSource = FakeAuthLocalDataSource(accessToken = null))

        val result = repository.getStoredMemberRole()

        assertInvalidRoleFailure(result)
    }

    @Test
    fun `getStoredMemberRole returns failure when stored access token payload is invalid`() = runBlocking {
        val repository = createRepository(localDataSource = FakeAuthLocalDataSource(accessToken = "header.%%%.signature"))

        val result = repository.getStoredMemberRole()

        assertInvalidRoleFailure(result)
    }

    @Test
    fun `clearSession clears local tokens current circle and member profile cache`() = runBlocking {
        val localDataSource = FakeAuthLocalDataSource(accessToken = jwtWithRoles("MEMBER"))
        val currentCircleLocalDataSource = FakeCurrentCircleLocalDataSource()
        val memberProfileLocalDataSource = FakeMemberProfileLocalDataSource()
        val repository = createRepository(
            localDataSource = localDataSource,
            currentCircleLocalDataSource = currentCircleLocalDataSource,
            memberProfileLocalDataSource = memberProfileLocalDataSource,
        )

        repository.clearSession()

        assertTrue(localDataSource.clearTokensCalled)
        assertTrue(currentCircleLocalDataSource.clearSelectedCircleIdCalled)
        assertTrue(memberProfileLocalDataSource.clearProfileCalled)
    }
}

private fun createRepository(
    accessToken: String = jwtWithRoles("MEMBER"),
    localDataSource: FakeAuthLocalDataSource = FakeAuthLocalDataSource(),
    currentCircleLocalDataSource: FakeCurrentCircleLocalDataSource = FakeCurrentCircleLocalDataSource(),
    memberProfileLocalDataSource: FakeMemberProfileLocalDataSource = FakeMemberProfileLocalDataSource(),
): AuthRepositoryImpl = AuthRepositoryImpl(
    localDataSource = localDataSource,
    currentCircleLocalDataSource = currentCircleLocalDataSource,
    memberProfileLocalDataSource = memberProfileLocalDataSource,
    remoteDataSource = FakeAuthRemoteDataSource(accessToken = accessToken),
)

private fun jwtWithRoles(vararg roles: String): String = jwtWithPayload(
    """{"roles":[${roles.joinToString(",") { "\"$it\"" }}]}""",
)

private fun assertInvalidRoleFailure(result: AppResult<MemberRole>) {
    assertTrue(result is AppResult.Failure)
    val error = (result as AppResult.Failure).error
    assertEquals(
        AppError.Unknown(message = "Access token roles claim is missing or invalid."),
        error,
    )
}

private fun jwtWithPayload(payload: String): String {
    val encodedPayload = Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(payload.toByteArray())
    return "header.$encodedPayload.signature"
}

private class FakeAuthLocalDataSource(
    private var accessToken: String? = null,
    private var refreshToken: String? = null,
) : AuthLocalDataSource {
    var clearTokensCalled: Boolean = false

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    override suspend fun getAccessToken(): String? = accessToken

    override suspend fun getRefreshToken(): String? = refreshToken

    override suspend fun hasSession(): Boolean = false

    override suspend fun clearTokens() {
        clearTokensCalled = true
        accessToken = null
        refreshToken = null
    }
}

private class FakeCurrentCircleLocalDataSource : CurrentCircleLocalDataSource {
    var clearSelectedCircleIdCalled: Boolean = false

    override fun observeSelectedCircleId(): Flow<String?> = flowOf(null)

    override suspend fun getSelectedCircleId(): String? = null

    override suspend fun saveSelectedCircleId(circleId: String?) = Unit

    override suspend fun clearSelectedCircleId() {
        clearSelectedCircleIdCalled = true
    }
}

private class FakeMemberProfileLocalDataSource : MemberProfileLocalDataSource {
    var clearProfileCalled: Boolean = false

    override suspend fun getProfile(): MemberProfileCache? = null

    override suspend fun saveProfile(profile: MemberProfileCache) = Unit

    override suspend fun clearProfile() {
        clearProfileCalled = true
    }
}

private class FakeAuthRemoteDataSource(private val accessToken: String) : AuthRemoteDataSource {
    override suspend fun loginWithKakao(idToken: String): ApiResponse<TokenResponse> = ApiResponse(
        statusCode = 200,
        isSuccess = true,
        data = TokenResponse(
            accessToken = accessToken,
            refreshToken = "refresh-token",
        ),
        errorCode = null,
        message = null,
        timestamp = "2026-07-05T00:00:00",
    )

    override suspend fun refreshToken(refreshToken: String): ApiResponse<TokenResponse> {
        throw UnsupportedOperationException("Not required for this test")
    }
}
