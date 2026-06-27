package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.MemberRemoteDataSource
import javax.inject.Inject

internal class MemberRepositoryImpl @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
    private val memberRemoteDataSource: MemberRemoteDataSource,
) : MemberRepository {
    override suspend fun getMyInfo(): AppResult<MemberInfo> = safeRequest(
        request = { memberRemoteDataSource.getMyInfo() },
    ) { response ->
        AppResult.Success(
            MemberInfo(
                role = response.role.toMemberRole(),
            ),
        )
    }

    override suspend fun completeTerms(): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.completeTerms() },
    ) {
        AppResult.Success(Unit)
    }

    override suspend fun completeNickname(nickname: String): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.completeNickname(nickname = nickname) },
    ) {
        AppResult.Success(Unit)
    }

    override suspend fun logout(): AppResult<Unit> = safeRequest(
        request = { memberRemoteDataSource.logout() },
    ) {
        authLocalDataSource.clearTokens()
        AppResult.Success(Unit)
    }
}

private fun String.toMemberRole(): MemberRole = when (uppercase()) {
    MemberRole.MEMBER.name -> MemberRole.MEMBER
    else -> MemberRole.PENDING
}
