package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.member.MemberInfoResponse

interface MemberRemoteDataSource {
    suspend fun getMyInfo(): ApiResponse<MemberInfoResponse>

    suspend fun completeTerms(): ApiResponse<Unit>

    suspend fun completeNickname(nickname: String): ApiResponse<Unit>

    suspend fun logout(): ApiResponse<Unit>
}
