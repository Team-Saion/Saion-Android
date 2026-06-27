package com.saion.core.domain.usecase.term

import com.saion.core.domain.repository.TermRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.term.Term
import javax.inject.Inject

class GetActiveTermsUseCase @Inject constructor(private val termRepository: TermRepository) {
    suspend operator fun invoke(): AppResult<List<Term>> = termRepository.getActiveTerms()
}
