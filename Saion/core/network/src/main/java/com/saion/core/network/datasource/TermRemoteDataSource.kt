package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.term.TermResponse

interface TermRemoteDataSource {
    suspend fun getActiveTerms(): ApiResponse<List<TermResponse>>
}
