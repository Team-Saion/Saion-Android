package com.saion.core.data.repository

import com.saion.core.datastore.datasource.CurrentCircleLocalDataSource
import com.saion.core.domain.repository.CircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrentCircleRepositoryImplTest {
    @Test
    fun `저장된 써클이 유효하면 그대로 유지한다`() = runBlocking {
        val localDataSource = MemoryCurrentCircleLocalDataSource(selectedCircleId = "circle-2")
        val repository = createRepository(
            localDataSource = localDataSource,
            circleRepository = FakeCircleRepository(
                result = AppResult.Success(
                    listOf(
                        CircleSummary(circleId = "circle-1", name = "하나", ownerId = "owner-1"),
                        CircleSummary(circleId = "circle-2", name = "둘", ownerId = "owner-2"),
                    ),
                ),
            ),
        )

        val result = repository.syncCurrentCircle()

        assertEquals(AppResult.Success("circle-2"), result)
        assertEquals("circle-2", localDataSource.selectedCircleId)
    }

    @Test
    fun `저장된 써클이 없으면 첫 번째 써클을 선택한다`() = runBlocking {
        val localDataSource = MemoryCurrentCircleLocalDataSource(selectedCircleId = null)
        val repository = createRepository(
            localDataSource = localDataSource,
            circleRepository = FakeCircleRepository(
                result = AppResult.Success(
                    listOf(
                        CircleSummary(circleId = "circle-1", name = "하나", ownerId = "owner-1"),
                        CircleSummary(circleId = "circle-2", name = "둘", ownerId = "owner-2"),
                    ),
                ),
            ),
        )

        val result = repository.syncCurrentCircle()

        assertEquals(AppResult.Success("circle-1"), result)
        assertEquals("circle-1", localDataSource.selectedCircleId)
    }

    @Test
    fun `저장된 써클이 무효하면 첫 번째 써클로 교체한다`() = runBlocking {
        val localDataSource = MemoryCurrentCircleLocalDataSource(selectedCircleId = "missing")
        val repository = createRepository(
            localDataSource = localDataSource,
            circleRepository = FakeCircleRepository(
                result = AppResult.Success(
                    listOf(CircleSummary(circleId = "circle-1", name = "하나", ownerId = "owner-1")),
                ),
            ),
        )

        val result = repository.syncCurrentCircle()

        assertEquals(AppResult.Success("circle-1"), result)
        assertEquals("circle-1", localDataSource.selectedCircleId)
    }

    @Test
    fun `써클이 비어 있으면 null 상태를 저장한다`() = runBlocking {
        val localDataSource = MemoryCurrentCircleLocalDataSource(selectedCircleId = "circle-1")
        val repository = createRepository(
            localDataSource = localDataSource,
            circleRepository = FakeCircleRepository(result = AppResult.Success(emptyList())),
        )

        val result = repository.syncCurrentCircle()

        assertEquals(AppResult.Success(null), result)
        assertEquals(null, localDataSource.selectedCircleId)
    }

    @Test
    fun `유효하지 않은 써클 선택은 실패를 반환한다`() = runBlocking {
        val repository = createRepository(
            localDataSource = MemoryCurrentCircleLocalDataSource(selectedCircleId = null),
            circleRepository = FakeCircleRepository(
                result = AppResult.Success(
                    listOf(CircleSummary(circleId = "circle-1", name = "하나", ownerId = "owner-1")),
                ),
            ),
        )

        val result = repository.selectCircle("circle-2")

        assertTrue(result is AppResult.Failure)
        assertEquals(
            AppError.Unknown(message = "Selected circle is missing or invalid."),
            (result as AppResult.Failure).error,
        )
    }
}

private fun createRepository(
    localDataSource: MemoryCurrentCircleLocalDataSource,
    circleRepository: FakeCircleRepository,
): CurrentCircleRepositoryImpl = CurrentCircleRepositoryImpl(
    currentCircleLocalDataSource = localDataSource,
    circleRepository = circleRepository,
)

private class MemoryCurrentCircleLocalDataSource(selectedCircleId: String?) : CurrentCircleLocalDataSource {
    private val flow = MutableStateFlow(selectedCircleId)

    val selectedCircleId: String?
        get() = flow.value

    override fun observeSelectedCircleId(): Flow<String?> = flow

    override suspend fun getSelectedCircleId(): String? = flow.value

    override suspend fun saveSelectedCircleId(circleId: String?) {
        flow.value = circleId
    }

    override suspend fun clearSelectedCircleId() {
        flow.value = null
    }
}

private class FakeCircleRepository(
    private val result: AppResult<List<CircleSummary>>,
) : CircleRepository {
    override suspend fun listCircles(): AppResult<List<CircleSummary>> = result

    override suspend fun createCircle(name: String): AppResult<CircleSummary> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary> {
        throw UnsupportedOperationException("Not required for this test")
    }
}
