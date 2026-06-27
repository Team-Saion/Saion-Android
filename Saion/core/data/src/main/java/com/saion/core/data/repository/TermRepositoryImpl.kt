package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.TermRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.term.Term
import com.saion.core.network.datasource.TermRemoteDataSource
import com.saion.core.network.model.term.TermResponse
import javax.inject.Inject

internal class TermRepositoryImpl @Inject constructor(
    private val termRemoteDataSource: TermRemoteDataSource,
) : TermRepository {
    override suspend fun getActiveTerms(): AppResult<List<Term>> = safeRequest(
        request = { termRemoteDataSource.getActiveTerms() },
    ) { response ->
        AppResult.Success(response.map(TermResponse::toDomain))
    }
}

private fun TermResponse.toDomain(): Term = Term(
    id = id,
    termCode = termCode,
    title = title,
    contentUrl = contentUrl,
    version = version,
    required = required,
    effectiveAt = effectiveAt,
)
