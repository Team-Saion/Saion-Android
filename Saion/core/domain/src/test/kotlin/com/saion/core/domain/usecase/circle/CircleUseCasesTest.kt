package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CircleUseCasesTest {
    @Test
    fun `써클 목록 조회는 저장소 결과를 그대로 반환한다`() = runBlocking {
        val expected = AppResult.Success(
            listOf(
                CircleSummary(circleId = "circle-1", name = "스터디", ownerId = "owner-1"),
            ),
        )
        val repository = FakeCircleRepository(listResult = expected)

        val actual = ListCirclesUseCase(repository).invoke()

        assertEquals(
            CircleUseCaseOutcome(
                result = expected,
                call = CircleRepositoryCall.ListCircles,
            ),
            CircleUseCaseOutcome(
                result = actual,
                call = repository.lastCall,
            ),
        )
    }

    @Test
    fun `써클 생성은 이름을 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(
            CircleSummary(circleId = "circle-1", name = "유니콘", ownerId = "owner-1"),
        )
        val repository = FakeCircleRepository(createResult = expected)

        val actual = CreateCircleUseCase(repository).invoke(name = "유니콘")

        assertEquals(
            CircleUseCaseOutcome(
                result = expected,
                call = CircleRepositoryCall.CreateCircle(name = "유니콘"),
            ),
            CircleUseCaseOutcome(
                result = actual,
                call = repository.lastCall,
            ),
        )
    }

    @Test
    fun `써클장 위임은 circleId와 대상 멤버를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(
            CircleSummary(circleId = "circle-1", name = "유니콘", ownerId = "member-2"),
        )
        val repository = FakeCircleRepository(transferResult = expected)

        val actual = TransferCircleInitiatorUseCase(repository).invoke(
            circleId = "circle-1",
            targetMemberId = "member-2",
        )

        assertEquals(
            CircleUseCaseOutcome(
                result = expected,
                call = CircleRepositoryCall.TransferInitiator(
                    circleId = "circle-1",
                    targetMemberId = "member-2",
                ),
            ),
            CircleUseCaseOutcome(
                result = actual,
                call = repository.lastCall,
            ),
        )
    }
}

private data class CircleUseCaseOutcome<T>(
    val result: AppResult<T>,
    val call: CircleRepositoryCall?,
)

private sealed interface CircleRepositoryCall {
    data object ListCircles : CircleRepositoryCall

    data class CreateCircle(val name: String) : CircleRepositoryCall

    data class TransferInitiator(
        val circleId: String,
        val targetMemberId: String,
    ) : CircleRepositoryCall
}

private class FakeCircleRepository(
    private val listResult: AppResult<List<CircleSummary>> = AppResult.Success(emptyList()),
    private val createResult: AppResult<CircleSummary> = AppResult.Success(
        CircleSummary(circleId = "default", name = "default", ownerId = "owner"),
    ),
    private val transferResult: AppResult<CircleSummary> = AppResult.Success(
        CircleSummary(circleId = "default", name = "default", ownerId = "owner"),
    ),
) : CircleRepository {
    var lastCall: CircleRepositoryCall? = null

    override suspend fun listCircles(): AppResult<List<CircleSummary>> {
        lastCall = CircleRepositoryCall.ListCircles
        return listResult
    }

    override suspend fun createCircle(name: String): AppResult<CircleSummary> {
        lastCall = CircleRepositoryCall.CreateCircle(name = name)
        return createResult
    }

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary> {
        lastCall = CircleRepositoryCall.TransferInitiator(
            circleId = circleId,
            targetMemberId = targetMemberId,
        )
        return transferResult
    }
}
