package com.saion.core.domain.usecase.term

import com.saion.core.domain.repository.TermRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 인증된 멤버의 약관 동의 내역을 저장합니다.
 */
class AgreeTermsUseCase @Inject constructor(private val termRepository: TermRepository) {
    suspend operator fun invoke(termIds: List<Long>): AppResult<Unit> = termRepository.agreeTerms(termIds = termIds)
}
