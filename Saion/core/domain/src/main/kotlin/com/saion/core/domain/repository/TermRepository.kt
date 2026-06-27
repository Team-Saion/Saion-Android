package com.saion.core.domain.repository

import com.saion.core.model.result.AppResult
import com.saion.core.model.term.Term

interface TermRepository {
    suspend fun getActiveTerms(): AppResult<List<Term>>
}
