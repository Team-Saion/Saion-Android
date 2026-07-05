package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.TermRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.term.Term
import com.saion.core.network.datasource.TermRemoteDataSource
import com.saion.core.network.model.term.TermResponse
import javax.inject.Inject

/**
 * 약관 전송 모델을 도메인 모델로 변환하는 기본 구현입니다.
 */
internal class TermRepositoryImpl @Inject constructor(private val termRemoteDataSource: TermRemoteDataSource) : TermRepository {
    override suspend fun getActiveTerms(): AppResult<List<Term>> = safeRequest(
        request = { termRemoteDataSource.getActiveTerms() },
    ) { response ->
        AppResult.Success(response.map(TermResponse::toDomain))
    }

    override suspend fun agreeTerms(termIds: List<Long>): AppResult<Unit> = safeRequest(
        request = { termRemoteDataSource.agreeTerms(termIds = termIds) },
    ) {
        AppResult.Success(Unit)
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
