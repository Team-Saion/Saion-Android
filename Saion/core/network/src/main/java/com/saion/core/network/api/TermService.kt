package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.term.TermResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class TermService(private val client: HttpClient) {
    suspend fun getActiveTerms(): ApiResponse<List<TermResponse>> = client
        .get("/v1/terms")
        .toApiResponse()
}
