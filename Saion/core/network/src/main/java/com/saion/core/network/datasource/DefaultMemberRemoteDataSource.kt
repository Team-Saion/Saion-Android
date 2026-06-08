package com.saion.core.network.datasource

import com.saion.core.network.api.MemberService
import com.saion.core.network.model.common.ApiResponse
import javax.inject.Inject

class DefaultMemberRemoteDataSource @Inject constructor(private val service: MemberService) : MemberRemoteDataSource {
    override suspend fun logout(): ApiResponse<Unit> = service.logout()
}
