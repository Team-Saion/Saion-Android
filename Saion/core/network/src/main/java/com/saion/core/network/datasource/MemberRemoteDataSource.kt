package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse

interface MemberRemoteDataSource {
    suspend fun logout(): ApiResponse<Unit>
}
