package com.saion.feature.circlecreate.impl

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.usecase.circle.CreateCircleUseCase
import com.saion.core.domain.usecase.circle.SelectCurrentCircleUseCase
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.result.BusinessErrorType
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateEffect
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateIntent
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateSnackbarMessage
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class CircleCreateViewModelTest {
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
    fun `유효한 이름을 입력하면 생성 버튼이 활성화된다`() = runTest {
        val viewModel = CircleCreateViewModel(
            createCircleUseCase = CreateCircleUseCase(FakeCircleRepository()),
            selectCurrentCircleUseCase = SelectCurrentCircleUseCase(FakeCurrentCircleRepository()),
        )

        viewModel.dispatch(CircleCreateIntent.NameChanged("비니네"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSubmitEnabled)
        assertEquals(null, viewModel.uiState.value.validationMessageResId)
    }

    @Test
    fun `금지 문자가 포함되면 생성 버튼이 비활성화된다`() = runTest {
        val viewModel = CircleCreateViewModel(
            createCircleUseCase = CreateCircleUseCase(FakeCircleRepository()),
            selectCurrentCircleUseCase = SelectCurrentCircleUseCase(FakeCurrentCircleRepository()),
        )

        viewModel.dispatch(CircleCreateIntent.NameChanged("비니<네"))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSubmitEnabled)
        assertEquals(R.string.circle_create_error_invalid_character, viewModel.uiState.value.validationMessageResId)
    }

    @Test
    fun `생성 성공 시 닫기 이펙트를 보낸다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository()
        val viewModel = CircleCreateViewModel(
            createCircleUseCase = CreateCircleUseCase(FakeCircleRepository()),
            selectCurrentCircleUseCase = SelectCurrentCircleUseCase(currentCircleRepository),
        )
        viewModel.dispatch(CircleCreateIntent.NameChanged("비니네"))
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(CircleCreateIntent.SubmitClicked)
        advanceUntilIdle()

        assertEquals(CircleCreateEffect.Close, effectDeferred.await())
        assertEquals("circle-1", currentCircleRepository.selectedCircleId)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `생성 실패 시 스낵바 이펙트를 보낸다`() = runTest {
        val viewModel = CircleCreateViewModel(
            createCircleUseCase = CreateCircleUseCase(
                FakeCircleRepository(
                    createResult = AppResult.Failure(
                        AppError.Business(
                            businessType = BusinessErrorType.DUPLICATE,
                            rawCode = "M409_1",
                            message = "이미 존재하는 이름입니다.",
                        ),
                    ),
                ),
            ),
            selectCurrentCircleUseCase = SelectCurrentCircleUseCase(FakeCurrentCircleRepository()),
        )
        viewModel.dispatch(CircleCreateIntent.NameChanged("비니네"))
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(CircleCreateIntent.SubmitClicked)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is CircleCreateEffect.ShowSnackbar)
        assertTrue((effect as CircleCreateEffect.ShowSnackbar).message is CircleCreateSnackbarMessage.Text)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }
}

private class FakeCircleRepository(
    private val createResult: AppResult<CircleSummary> = AppResult.Success(
        CircleSummary(
            circleId = "circle-1",
            name = "비니네",
            ownerId = "owner-1",
        ),
    ),
) : CircleRepository {
    override suspend fun listCircles(): AppResult<List<CircleSummary>> = error("Not used")

    override suspend fun createCircle(name: String): AppResult<CircleSummary> = createResult

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary> = error("Not used")
}

private class FakeCurrentCircleRepository : CurrentCircleRepository {
    private val flow = MutableStateFlow<String?>(null)

    val selectedCircleId: String?
        get() = flow.value

    override fun observeCurrentCircleId(): Flow<String?> = flow

    override suspend fun getCurrentCircleId(): String? = flow.value

    override suspend fun selectCircle(circleId: String): AppResult<Unit> {
        flow.value = circleId
        return AppResult.Success(Unit)
    }

    override suspend fun syncCurrentCircle(): AppResult<String?> = AppResult.Success(flow.value)

    override suspend fun clearCurrentCircle() {
        flow.value = null
    }
}
