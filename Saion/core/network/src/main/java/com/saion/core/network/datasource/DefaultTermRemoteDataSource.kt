package com.saion.core.network.datasource

import com.saion.core.network.api.TermService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.term.TermResponse
import javax.inject.Inject

class DefaultTermRemoteDataSource @Inject constructor(private val service: TermService) : TermRemoteDataSource {
    override suspend fun getActiveTerms(): ApiResponse<List<TermResponse>> = service.getActiveTerms()
}
