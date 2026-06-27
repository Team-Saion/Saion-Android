package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.member.MemberInfoResponse

interface MemberRemoteDataSource {
    suspend fun getMyInfo(): ApiResponse<MemberInfoResponse>

    suspend fun updateProfile(nickname: String): ApiResponse<MemberInfoResponse>

    suspend fun uploadProfileImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): ApiResponse<MemberInfoResponse>

    suspend fun logout(): ApiResponse<Unit>

    suspend fun withdraw(): ApiResponse<Unit>
}
