package com.saion.core.domain.usecase.term

import com.saion.core.domain.repository.TermRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 멤버의 약관 동의 내역을 저장합니다.
 *
 * 온보딩 전에 필수 약관 동의를 서버에 확정할 때 사용합니다.
 */
class AgreeTermsUseCase @Inject constructor(private val termRepository: TermRepository) {
    suspend operator fun invoke(termIds: List<Long>): AppResult<Unit> = termRepository.agreeTerms(termIds = termIds)
}
