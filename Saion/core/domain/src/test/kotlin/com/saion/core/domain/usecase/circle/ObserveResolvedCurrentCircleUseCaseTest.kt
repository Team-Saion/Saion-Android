package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveResolvedCurrentCircleUseCaseTest {
    @Test
    fun `현재 써클이 있으면 그대로 반환한다`() = runTest {
        val repository = FakeCurrentCircleRepository(initialCircleId = "circle-1")

        val actual = observeResolvedCurrentCircleUseCase(repository)().first()

        assertEquals(ResolvedCurrentCircle.Available("circle-1"), actual)
        assertEquals(0, repository.syncCallCount)
    }

    @Test
    fun `현재 써클이 없고 sync로 복구되지 않으면 missing을 반환한다`() = runTest {
        val repository = FakeCurrentCircleRepository(initialCircleId = null)

        val actual = observeResolvedCurrentCircleUseCase(repository)().first()

        assertEquals(ResolvedCurrentCircle.Missing, actual)
        assertEquals(1, repository.syncCallCount)
    }

    @Test
    fun `현재 써클이 없더라도 sync가 복구하면 복구된 circle id를 반환한다`() = runTest {
        val repository = FakeCurrentCircleRepository(
            initialCircleId = null,
            syncedCircleId = "circle-2",
        )

        val actual = observeResolvedCurrentCircleUseCase(repository)().first()

        assertEquals(ResolvedCurrentCircle.Available("circle-2"), actual)
        assertEquals(1, repository.syncCallCount)
    }

    @Test
    fun `sync 실패 시 missing을 반환한다`() = runTest {
        val repository = FakeCurrentCircleRepository(
            initialCircleId = null,
            syncResult = AppResult.Failure(ThrowableAppError),
        )

        val actual = observeResolvedCurrentCircleUseCase(repository)().first()

        assertEquals(ResolvedCurrentCircle.Missing, actual)
        assertEquals(1, repository.syncCallCount)
    }
}

private fun observeResolvedCurrentCircleUseCase(
    repository: CurrentCircleRepository,
): ObserveResolvedCurrentCircleUseCase = ObserveResolvedCurrentCircleUseCase(
    observeCurrentCircleUseCase = ObserveCurrentCircleUseCase(repository),
    syncCurrentCircleUseCase = SyncCurrentCircleUseCase(repository),
)

private class FakeCurrentCircleRepository(
    initialCircleId: String?,
    private val syncedCircleId: String? = initialCircleId,
    private val syncResult: AppResult<String?>? = null,
) : CurrentCircleRepository {
    private val flow = MutableStateFlow(initialCircleId)
    var syncCallCount: Int = 0

    override fun observeCurrentCircleId(): StateFlow<String?> = flow

    override suspend fun getCurrentCircleId(): String? = flow.value

    override suspend fun selectCircle(circleId: String): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun syncCurrentCircle(): AppResult<String?> {
        syncCallCount += 1
        syncResult?.let { return it }
        flow.value = syncedCircleId
        return AppResult.Success(flow.value)
    }

    override suspend fun clearCurrentCircle() {
        flow.value = null
    }
}

private val ThrowableAppError = com.saion.core.model.result.AppError.Unknown(message = "sync failed")
