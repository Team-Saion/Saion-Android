package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.term.TermResponse

/**
 * 약관 관련 원격 데이터 소스 계약입니다.
 */
interface TermRemoteDataSource {
    /**
     * 현재 발효 중인 약관 목록을 조회합니다.
     */
    suspend fun getActiveTerms(): ApiResponse<List<TermResponse>>

    /**
     * 현재 인증된 멤버의 약관 동의 내역을 저장합니다.
     */
    suspend fun agreeTerms(termIds: List<Long>): ApiResponse<Unit>
}
