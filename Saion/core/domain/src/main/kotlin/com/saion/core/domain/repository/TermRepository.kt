package com.saion.core.domain.repository

import com.saion.core.model.result.AppResult
import com.saion.core.model.term.Term

/**
 * 약관 도메인 경계를 정의합니다.
 */
interface TermRepository {
    /**
     * 현재 발효 중인 약관 목록을 조회합니다.
     */
    suspend fun getActiveTerms(): AppResult<List<Term>>
}
