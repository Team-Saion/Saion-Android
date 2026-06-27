package com.saion.core.network.datasource

import com.saion.core.network.api.MemberService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.member.MemberInfoResponse
import javax.inject.Inject

class DefaultMemberRemoteDataSource @Inject constructor(private val service: MemberService) : MemberRemoteDataSource {
    override suspend fun getMyInfo(): ApiResponse<MemberInfoResponse> = service.getMyInfo()

    override suspend fun updateProfile(nickname: String): ApiResponse<MemberInfoResponse> = service.updateProfile(nickname = nickname)

    override suspend fun uploadProfileImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): ApiResponse<MemberInfoResponse> = service.uploadProfileImage(
        imageBytes = imageBytes,
        fileName = fileName,
        mimeType = mimeType,
    )

    override suspend fun logout(): ApiResponse<Unit> = service.logout()

    override suspend fun withdraw(): ApiResponse<Unit> = service.withdraw()
}
