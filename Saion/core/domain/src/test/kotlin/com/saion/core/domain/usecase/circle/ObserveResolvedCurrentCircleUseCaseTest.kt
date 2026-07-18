package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
    repository: FakeCurrentCircleRepository,
): ObserveResolvedCurrentCircleUseCase = ObserveResolvedCurrentCircleUseCase(
    observeCurrentCircleUseCase = ObserveCurrentCircleUseCase(repository),
    syncCurrentCircleUseCase = SyncCurrentCircleUseCase(repository, FakeResolvedCircleRepository(repository)),
)

private class FakeCurrentCircleRepository(
    initialCircleId: String?,
    val syncedCircleId: String? = initialCircleId,
    val syncResult: AppResult<String?>? = null,
) : CurrentCircleRepository {
    private val flow = MutableStateFlow(initialCircleId)
    var syncCallCount: Int = 0

    override fun observeCurrentCircleId(): StateFlow<String?> = flow

    override suspend fun getCurrentCircleId(): String? = flow.value

    override suspend fun selectCircle(circleId: String): AppResult<Unit> {
        flow.value = circleId
        return AppResult.Success(Unit)
    }

    override suspend fun clearCurrentCircle() {
        flow.value = null
    }
}

private class FakeResolvedCircleRepository(
    private val repository: FakeCurrentCircleRepository,
) : CircleRepository {
    override fun observeCircles(): Flow<List<CircleSummary>> = flowOf(emptyList())

    override suspend fun listCircles(): AppResult<List<CircleSummary>> = AppResult.Success(emptyList())

    override suspend fun refreshCircles(): AppResult<List<CircleSummary>> {
        repository.syncCallCount += 1
        return when (val result = repository.syncResult) {
            is AppResult.Success -> AppResult.Success(
                result.data?.let { listOf(CircleSummary(circleId = it, name = "circle", ownerId = "owner")) }.orEmpty(),
            )

            is AppResult.Failure -> AppResult.Failure(result.error)
            null -> AppResult.Success(
                repository.syncedCircleId?.let { listOf(CircleSummary(circleId = it, name = "circle", ownerId = "owner")) }.orEmpty(),
            )
        }
    }

    override suspend fun createCircle(name: String): AppResult<CircleSummary> = error("Not required for this test")

    override suspend fun transferInitiator(circleId: String, targetMemberId: String): AppResult<CircleSummary> =
        error("Not required for this test")

    override suspend fun leave(circleId: String): AppResult<Unit> = error("Not required for this test")
}

private val ThrowableAppError = com.saion.core.model.result.AppError.Unknown(message = "sync failed")
