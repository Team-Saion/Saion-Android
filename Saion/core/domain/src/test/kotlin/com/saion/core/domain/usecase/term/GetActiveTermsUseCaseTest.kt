package com.saion.core.domain.usecase.term

import com.saion.core.domain.repository.TermRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.term.Term
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetActiveTermsUseCaseTest {
    @Test
    fun `활성 약관 조회 유스케이스는 저장소 결과를 그대로 반환한다`() = runBlocking {
        val expected = listOf(
            Term(
                id = "term-1",
                termCode = "SERVICE",
                title = "Service Terms",
                contentUrl = null,
                version = 1,
                required = true,
                effectiveAt = "2026-06-28T00:00:00",
            ),
        )
        val repository = FakeTermRepository(result = AppResult.Success(expected))
        val useCase = GetActiveTermsUseCase(termRepository = repository)

        val result = useCase()

        assertEquals(AppResult.Success(expected), result)
    }
}

private class FakeTermRepository(private val result: AppResult<List<Term>>) : TermRepository {
    override suspend fun getActiveTerms(): AppResult<List<Term>> = result
}
