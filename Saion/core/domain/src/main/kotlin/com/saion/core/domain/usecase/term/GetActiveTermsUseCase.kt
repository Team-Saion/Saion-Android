package com.saion.core.domain.usecase.term

import com.saion.core.domain.repository.TermRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.term.Term
import javax.inject.Inject

/**
 * 현재 발효 중인 약관 목록을 조회합니다.
 */
class GetActiveTermsUseCase @Inject constructor(private val termRepository: TermRepository) {
    suspend operator fun invoke(): AppResult<List<Term>> = termRepository.getActiveTerms()
}
