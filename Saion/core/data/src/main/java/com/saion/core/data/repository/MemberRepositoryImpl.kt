package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.ProfileImageUpload
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

private fun String.toMemberRole(): MemberRole = when (uppercase()) {
    MemberRole.MEMBER.name -> MemberRole.MEMBER
    MemberRole.ADMIN.name -> MemberRole.ADMIN
    else -> MemberRole.PENDING
}
