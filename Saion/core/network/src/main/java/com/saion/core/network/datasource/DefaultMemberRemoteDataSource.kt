package com.saion.core.network.datasource

import com.saion.core.network.api.MemberService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.member.MemberInfoResponse
import javax.inject.Inject

class DefaultMemberRemoteDataSource @Inject constructor(private val service: MemberService) : MemberRemoteDataSource {
    override suspend fun getMyInfo(): ApiResponse<MemberInfoResponse> = service.getMyInfo()

    override suspend fun completeTerms(): ApiResponse<Unit> = service.completeTerms()

    override suspend fun completeNickname(nickname: String): ApiResponse<Unit> = service.completeNickname(nickname = nickname)

    override suspend fun logout(): ApiResponse<Unit> = service.logout()
}
