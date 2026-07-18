package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.HomeRepository
import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationOption
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.CreateScheduleCommand
import com.saion.core.model.schedule.CreatedSchedule
import com.saion.core.model.schedule.RegisteredConfirmation
import com.saion.core.model.schedule.ScheduleDetail
import com.saion.core.model.schedule.ScheduleListPage
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUpdateValue
import com.saion.core.model.schedule.UpdateScheduleCommand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ScheduleUseCasesTest {
    @Test
    fun `일정 목록 조회는 cursor와 size를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(ScheduleListPage(schedules = emptyList(), nextCursor = "next", hasNext = true))
        val repository = FakeScheduleRepository(listResult = expected)

        val actual = GetScheduleListUseCase(repository).invoke(circleId = "circle-1", cursor = "cursor-1", size = 20)

        assertEquals(
            ScheduleUseCaseOutcome(
                result = expected,
                call = ScheduleRepositoryCall.GetScheduleList(circleId = "circle-1", cursor = "cursor-1", size = 20),
            ),
            ScheduleUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `일정 생성은 command를 그대로 저장소에 전달한다`() = runBlocking {
        val command = CreateScheduleCommand(
            title = "여행",
            startDate = "2026-07-08",
            endDate = "2026-07-09",
            startTime = "09:00",
            endTime = "10:00",
            needConfirm = true,
            memo = "메모",
        )
        val expected = AppResult.Success(CreatedSchedule(scheduleId = "schedule-1"))
        val repository = FakeScheduleRepository(createResult = expected)
        val homeRepository = FakeHomeRepository()

        val actual = CreateScheduleUseCase(repository, homeRepository).invoke(circleId = "circle-1", command = command)

        assertEquals(
            ScheduleUseCaseOutcome(
                result = expected,
                call = ScheduleRepositoryCall.CreateSchedule(circleId = "circle-1", command = command),
            ),
            ScheduleUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `일정 상세 조회는 식별자를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(defaultScheduleDetail())
        val repository = FakeScheduleRepository(detailResult = expected)

        val actual = GetScheduleDetailUseCase(repository).invoke(circleId = "circle-1", scheduleId = "schedule-1")

        assertEquals(
            ScheduleUseCaseOutcome(
                result = expected,
                call = ScheduleRepositoryCall.GetScheduleDetail(circleId = "circle-1", scheduleId = "schedule-1"),
            ),
            ScheduleUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `일정 수정은 tri-state command를 그대로 저장소에 전달한다`() = runBlocking {
        val command = UpdateScheduleCommand(
            title = ScheduleUpdateValue.Set("수정 제목"),
            startTime = ScheduleUpdateValue.Set(null),
            endTime = ScheduleUpdateValue.Set(null),
            needConfirm = false,
        )
        val expected = AppResult.Success(Unit)
        val repository = FakeScheduleRepository(updateResult = expected)

        val actual = UpdateScheduleUseCase(repository).invoke(
            circleId = "circle-1",
            scheduleId = "schedule-1",
            command = command,
        )

        assertEquals(
            ScheduleUseCaseOutcome(
                result = expected,
                call = ScheduleRepositoryCall.UpdateSchedule(
                    circleId = "circle-1",
                    scheduleId = "schedule-1",
                    command = command,
                ),
            ),
            ScheduleUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `일정 삭제는 식별자를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(Unit)
        val repository = FakeScheduleRepository(deleteResult = expected)
        val homeRepository = FakeHomeRepository()

        val actual = DeleteScheduleUseCase(repository, homeRepository).invoke(circleId = "circle-1", scheduleId = "schedule-1")

        assertEquals(
            ScheduleUseCaseOutcome(
                result = expected,
                call = ScheduleRepositoryCall.DeleteSchedule(circleId = "circle-1", scheduleId = "schedule-1"),
            ),
            ScheduleUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `확인 타입 조회는 circleId를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(
            listOf(ConfirmationOption(value = ConfirmationType.CONFIRMED, label = "확인했어요")),
        )
        val repository = FakeScheduleRepository(confirmationTypesResult = expected)

        val actual = GetConfirmationTypesUseCase(repository).invoke(circleId = "circle-1")

        assertEquals(
            ScheduleUseCaseOutcome(
                result = expected,
                call = ScheduleRepositoryCall.GetConfirmationTypes(circleId = "circle-1"),
            ),
            ScheduleUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `확인 등록은 confirmation type을 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(RegisteredConfirmation(confirmationType = ConfirmationType.CONFIRMED))
        val repository = FakeScheduleRepository(registerResult = expected)

        val actual = RegisterConfirmationUseCase(repository).invoke(
            circleId = "circle-1",
            scheduleId = "schedule-1",
            confirmationType = ConfirmationType.CONFIRMED,
        )

        assertEquals(
            ScheduleUseCaseOutcome(
                result = expected,
                call = ScheduleRepositoryCall.RegisterConfirmation(
                    circleId = "circle-1",
                    scheduleId = "schedule-1",
                    confirmationType = ConfirmationType.CONFIRMED,
                ),
            ),
            ScheduleUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `확인 취소는 confirmationId를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(Unit)
        val repository = FakeScheduleRepository(cancelResult = expected)

        val actual = CancelConfirmationUseCase(repository).invoke(
            circleId = "circle-1",
            scheduleId = "schedule-1",
            confirmationId = 1L,
        )

        assertEquals(
            ScheduleUseCaseOutcome(
                result = expected,
                call = ScheduleRepositoryCall.CancelConfirmation(
                    circleId = "circle-1",
                    scheduleId = "schedule-1",
                    confirmationId = 1L,
                ),
            ),
            ScheduleUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `일정 생성 실패 시 홈은 새로고침하지 않는다`() = runBlocking {
        val repository = FakeScheduleRepository(
            createResult = AppResult.Failure(com.saion.core.model.result.AppError.NetworkUnavailable()),
        )
        val homeRepository = FakeHomeRepository()

        CreateScheduleUseCase(repository, homeRepository).invoke(
            circleId = "circle-1",
            command = CreateScheduleCommand(
                title = "여행",
                startDate = "2026-07-08",
                endDate = "2026-07-09",
                startTime = "09:00",
                endTime = "10:00",
                needConfirm = true,
                memo = "메모",
            ),
        )

        assertEquals(emptyList<String>(), homeRepository.refreshedCircleIds)
    }

    @Test
    fun `일정 삭제 실패 시 홈은 새로고침하지 않는다`() = runBlocking {
        val repository = FakeScheduleRepository(
            deleteResult = AppResult.Failure(com.saion.core.model.result.AppError.NetworkUnavailable()),
        )
        val homeRepository = FakeHomeRepository()

        DeleteScheduleUseCase(repository, homeRepository).invoke(circleId = "circle-1", scheduleId = "schedule-1")

        assertEquals(emptyList<String>(), homeRepository.refreshedCircleIds)
    }
}

private data class ScheduleUseCaseOutcome<T>(
    val result: AppResult<T>,
    val call: ScheduleRepositoryCall?,
)

private sealed interface ScheduleRepositoryCall {
    data class GetScheduleList(val circleId: String, val cursor: String?, val size: Int?) : ScheduleRepositoryCall

    data class CreateSchedule(val circleId: String, val command: CreateScheduleCommand) : ScheduleRepositoryCall

    data class GetScheduleDetail(val circleId: String, val scheduleId: String) : ScheduleRepositoryCall

    data class UpdateSchedule(val circleId: String, val scheduleId: String, val command: UpdateScheduleCommand) : ScheduleRepositoryCall

    data class DeleteSchedule(val circleId: String, val scheduleId: String) : ScheduleRepositoryCall

    data class GetConfirmationTypes(val circleId: String) : ScheduleRepositoryCall

    data class RegisterConfirmation(
        val circleId: String,
        val scheduleId: String,
        val confirmationType: ConfirmationType,
    ) : ScheduleRepositoryCall

    data class CancelConfirmation(
        val circleId: String,
        val scheduleId: String,
        val confirmationId: Long,
    ) : ScheduleRepositoryCall
}

private class FakeScheduleRepository(
    private val listResult: AppResult<ScheduleListPage> = AppResult.Success(ScheduleListPage(emptyList(), null, false)),
    private val createResult: AppResult<CreatedSchedule> = AppResult.Success(CreatedSchedule(scheduleId = "default")),
    private val detailResult: AppResult<ScheduleDetail> = AppResult.Success(defaultScheduleDetail()),
    private val updateResult: AppResult<Unit> = AppResult.Success(Unit),
    private val deleteResult: AppResult<Unit> = AppResult.Success(Unit),
    private val confirmationTypesResult: AppResult<List<ConfirmationOption>> = AppResult.Success(emptyList()),
    private val registerResult: AppResult<RegisteredConfirmation> = AppResult.Success(
        RegisteredConfirmation(confirmationType = ConfirmationType.CONFIRMED),
    ),
    private val cancelResult: AppResult<Unit> = AppResult.Success(Unit),
) : ScheduleRepository {
    var lastCall: ScheduleRepositoryCall? = null

    override fun observeScheduleList(circleId: String): Flow<ScheduleListPage?> = flowOf(null)

    override fun observeScheduleDetail(circleId: String, scheduleId: String): Flow<ScheduleDetail?> = flowOf(null)

    override suspend fun getCachedScheduleList(circleId: String): ScheduleListPage? = null

    override suspend fun getScheduleList(circleId: String, cursor: String?, size: Int?): AppResult<ScheduleListPage> {
        lastCall = ScheduleRepositoryCall.GetScheduleList(circleId = circleId, cursor = cursor, size = size)
        return listResult
    }

    override suspend fun refreshScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): AppResult<ScheduleListPage> = getScheduleList(circleId, cursor, size)

    override suspend fun createSchedule(circleId: String, command: CreateScheduleCommand): AppResult<CreatedSchedule> {
        lastCall = ScheduleRepositoryCall.CreateSchedule(circleId = circleId, command = command)
        return createResult
    }

    override suspend fun getScheduleDetail(circleId: String, scheduleId: String): AppResult<ScheduleDetail> {
        lastCall = ScheduleRepositoryCall.GetScheduleDetail(circleId = circleId, scheduleId = scheduleId)
        return detailResult
    }

    override suspend fun getCachedScheduleDetail(circleId: String, scheduleId: String): ScheduleDetail? = null

    override suspend fun refreshScheduleDetail(circleId: String, scheduleId: String): AppResult<ScheduleDetail> =
        getScheduleDetail(circleId, scheduleId)

    override suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        command: UpdateScheduleCommand,
    ): AppResult<Unit> {
        lastCall = ScheduleRepositoryCall.UpdateSchedule(circleId = circleId, scheduleId = scheduleId, command = command)
        return updateResult
    }

    override suspend fun deleteSchedule(circleId: String, scheduleId: String): AppResult<Unit> {
        lastCall = ScheduleRepositoryCall.DeleteSchedule(circleId = circleId, scheduleId = scheduleId)
        return deleteResult
    }

    override suspend fun getConfirmationTypes(circleId: String): AppResult<List<ConfirmationOption>> {
        lastCall = ScheduleRepositoryCall.GetConfirmationTypes(circleId = circleId)
        return confirmationTypesResult
    }

    override suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: ConfirmationType,
    ): AppResult<RegisteredConfirmation> {
        lastCall = ScheduleRepositoryCall.RegisterConfirmation(
            circleId = circleId,
            scheduleId = scheduleId,
            confirmationType = confirmationType,
        )
        return registerResult
    }

    override suspend fun cancelConfirmation(circleId: String, scheduleId: String, confirmationId: Long): AppResult<Unit> {
        lastCall = ScheduleRepositoryCall.CancelConfirmation(
            circleId = circleId,
            scheduleId = scheduleId,
            confirmationId = confirmationId,
        )
        return cancelResult
    }
}

private class FakeHomeRepository : HomeRepository {
    val refreshedCircleIds = mutableListOf<String>()

    override fun observeHome(circleId: String): Flow<HomeOverview?> = flowOf(null)

    override fun observeMembers(circleId: String): Flow<List<CircleMember>> = flowOf(emptyList())

    override suspend fun getHome(circleId: String): AppResult<HomeOverview> = error("Not used")

    override suspend fun refreshHome(circleId: String): AppResult<HomeOverview> {
        refreshedCircleIds += circleId
        return AppResult.Failure(com.saion.core.model.result.AppError.NetworkUnavailable())
    }

    override suspend fun getMembers(circleId: String): AppResult<List<CircleMember>> = error("Not used")

    override suspend fun updateCachedMyMemberProfile(
        circleId: String,
        memberInfo: com.saion.core.model.member.MemberInfo,
    ) = Unit
}

private fun defaultScheduleDetail(): ScheduleDetail = ScheduleDetail(
    scheduleId = "schedule-1",
    title = "여행",
    startDate = "2026-07-08",
    endDate = "2026-07-09",
    startTime = "09:00",
    endTime = "10:00",
    isAllDay = false,
    needConfirm = true,
    status = ScheduleStatus.UPCOMING,
    progressRate = 0,
    dday = 1,
    memo = "메모",
    confirmations = emptyList(),
    myConfirmation = null,
    createdBy = "member-1",
    createdAt = "2026-07-08T00:00:00",
)
