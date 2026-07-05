package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.term.TermResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get

/**
 * Term API 원격 호출을 구성합니다.
 */
class TermService(private val client: HttpClient) {
    /**
     * 현재 발효 중인 약관을 `termCode`별 최신 버전 하나씩 조회합니다.
     *
     * 로그인 전에도 호출할 수 있는 공개 API이며, 발효된 약관이 없으면 빈 배열을 반환합니다.
     */
    suspend fun getActiveTerms(): ApiResponse<List<TermResponse>> = client
        .get("/api/v1/terms")
        .toApiResponse()
}
