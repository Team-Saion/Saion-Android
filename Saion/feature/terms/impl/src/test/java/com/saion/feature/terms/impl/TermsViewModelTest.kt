package com.saion.feature.terms.impl

import com.saion.core.domain.repository.TermRepository
import com.saion.core.domain.usecase.term.AgreeTermsUseCase
import com.saion.core.domain.usecase.term.GetActiveTermsUseCase
import com.saion.core.model.result.AppResult
import com.saion.core.model.term.Term
import com.saion.feature.terms.impl.viewmodel.TermsUIEffect
import com.saion.feature.terms.impl.viewmodel.TermsUIIntent
import com.saion.feature.terms.impl.viewmodel.TermsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TermsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `약관 상세 열기 요청 시 상세 이동 effect를 보낸다`() = runTest {
        val term = sampleTerm()
        val viewModel = createViewModel(
            repository = FakeTermRepository(
                activeTermsResult = AppResult.Success(listOf(term)),
            ),
        )
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(TermsUIIntent.OpenTerm(term.id))
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is TermsUIEffect.NavigateDetail)
        effect as TermsUIEffect.NavigateDetail
        assertEquals(term.title, effect.title)
        assertEquals(term.contentUrl, effect.url)
    }

    @Test
    fun `필수 약관 동의 후 제출 시 완료 effect를 보낸다`() = runTest {
        val term = sampleTerm(required = true)
        val repository = FakeTermRepository(
            activeTermsResult = AppResult.Success(listOf(term)),
            agreeTermsResult = AppResult.Success(Unit),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(TermsUIIntent.ToggleTerm(term.id))
        viewModel.dispatch(TermsUIIntent.SubmitAgreements)
        advanceUntilIdle()

        assertEquals(listOf(term.id.toLong()), repository.agreedTermIds)
        assertEquals(TermsUIEffect.NavigateComplete, effectDeferred.await())
    }

    private fun createViewModel(repository: FakeTermRepository): TermsViewModel = TermsViewModel(
        getActiveTermsUseCase = GetActiveTermsUseCase(repository),
        agreeTermsUseCase = AgreeTermsUseCase(repository),
    )

    private fun sampleTerm(
        id: String = "1",
        required: Boolean = true,
    ): Term = Term(
        id = id,
        termCode = "SERVICE",
        title = "서비스 이용약관",
        contentUrl = "https://example.com/terms",
        version = 1,
        required = required,
        effectiveAt = "2026-07-17T00:00:00",
    )
}

private class FakeTermRepository(
    private val activeTermsResult: AppResult<List<Term>>,
    private val agreeTermsResult: AppResult<Unit> = AppResult.Success(Unit),
) : TermRepository {
    var agreedTermIds: List<Long> = emptyList()

    override suspend fun getActiveTerms(): AppResult<List<Term>> = activeTermsResult

    override suspend fun agreeTerms(termIds: List<Long>): AppResult<Unit> {
        agreedTermIds = termIds
        return agreeTermsResult
    }
}
