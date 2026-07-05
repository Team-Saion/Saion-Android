package com.saion.core.network.datasource

import com.saion.core.network.api.TermService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.term.TermResponse
import javax.inject.Inject

/**
 * [TermService]를 위임하는 기본 약관 원격 데이터 소스입니다.
 */
class DefaultTermRemoteDataSource @Inject constructor(private val service: TermService) : TermRemoteDataSource {
    override suspend fun getActiveTerms(): ApiResponse<List<TermResponse>> = service.getActiveTerms()
}
