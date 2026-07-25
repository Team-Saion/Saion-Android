package com.saion.feature.circlecreate.impl

import com.saion.feature.circlecreate.impl.viewmodel.CircleJoinEffect
import com.saion.feature.circlecreate.impl.viewmodel.CircleJoinIntent
import com.saion.feature.circlecreate.impl.viewmodel.CircleJoinViewModel
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CircleJoinViewModelTest {
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
    fun `유효한 초대 코드를 입력하면 참여 버튼이 활성화된다`() = runTest {
        val viewModel = CircleJoinViewModel()

        viewModel.dispatch(CircleJoinIntent.CodeChanged("invite-token"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSubmitEnabled)
        assertEquals(null, viewModel.uiState.value.validationMessageResId)
    }

    @Test
    fun `빈 초대 코드를 제출하면 검증 에러가 표시된다`() = runTest {
        val viewModel = CircleJoinViewModel()

        viewModel.dispatch(CircleJoinIntent.SubmitClicked)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSubmitEnabled)
        assertEquals(R.string.circle_join_error_required, viewModel.uiState.value.validationMessageResId)
    }

    @Test
    fun `초대 코드 제출 시 trim 된 토큰으로 초대장 열기 이펙트를 보낸다`() = runTest {
        val viewModel = CircleJoinViewModel()
        viewModel.dispatch(CircleJoinIntent.CodeChanged("  invite-token  "))
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(CircleJoinIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(CircleJoinEffect.OpenInvitation(token = "invite-token"), effectDeferred.await())
    }
}
