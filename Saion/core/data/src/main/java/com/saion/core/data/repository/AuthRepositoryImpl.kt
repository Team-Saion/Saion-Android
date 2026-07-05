package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.domain.repository.AuthRepository
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.AuthRemoteDataSource
import java.util.Base64
import javax.inject.Inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * 인증 응답을 로컬 세션 저장소와 결합하는 기본 구현입니다.
 */
internal class AuthRepositoryImpl @Inject constructor(
    private val localDataSource: AuthLocalDataSource,
    private val remoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
    override suspend fun loginWithKakao(idToken: String): AppResult<MemberRole> = safeRequest(
        request = { remoteDataSource.loginWithKakao(idToken = idToken) },
    ) { tokenData ->
        localDataSource.saveTokens(
            accessToken = tokenData.accessToken,
            refreshToken = tokenData.refreshToken,
        )
        tokenData.accessToken.extractMemberRoleFromJwt()
            ?.let { role -> AppResult.Success(role) }
            ?: AppResult.Failure(AppError.Unknown(message = "Access token roles claim is missing or invalid."))
    }

    override suspend fun isSignedIn(): Boolean = localDataSource.hasSession()
}

private fun String.extractMemberRoleFromJwt(): MemberRole? = runCatching {
    val jwtParts = split(".")
    if (jwtParts.size != 3) return null

    val payload = Base64.getUrlDecoder()
        .decode(jwtParts[1])
        .decodeToString()

    Json.parseToJsonElement(payload)
        .jsonObject["roles"]
        ?.jsonArray
        ?.firstOrNull()
        ?.jsonPrimitive
        ?.contentOrNull
        ?.let { role -> MemberRole.from(role) }
}.getOrNull()
