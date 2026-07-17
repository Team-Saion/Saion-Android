package com.saion.core.domain.usecase.home

import com.saion.core.domain.repository.HomeRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeUseCasesTest {
    @Test
    fun `홈 조회는 circleId를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(defaultHomeOverview())
        val repository = FakeHomeRepository(homeResult = expected)

        val actual = GetHomeUseCase(repository).invoke(circleId = "circle-1")

        assertEquals(
            HomeUseCaseOutcome(
                result = expected,
                call = HomeRepositoryCall.GetHome(circleId = "circle-1"),
            ),
            HomeUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `홈 멤버 조회는 circleId를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(
            listOf(
                CircleMember(
                    memberId = "member-1",
                    nickname = "닉네임",
                    avatarColor = "#FFFFFF",
                    profileImageUrl = null,
                    isMe = true,
                    role = "MEMBER",
                ),
            ),
        )
        val repository = FakeHomeRepository(membersResult = expected)

        val actual = GetHomeMembersUseCase(repository).invoke(circleId = "circle-1")

        assertEquals(
            HomeUseCaseOutcome(
                result = expected,
                call = HomeRepositoryCall.GetMembers(circleId = "circle-1"),
            ),
            HomeUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }
}

private data class HomeUseCaseOutcome<T>(
    val result: AppResult<T>,
    val call: HomeRepositoryCall?,
)

private sealed interface HomeRepositoryCall {
    data class GetHome(val circleId: String) : HomeRepositoryCall

    data class GetMembers(val circleId: String) : HomeRepositoryCall
}

private class FakeHomeRepository(
    private val homeResult: AppResult<HomeOverview> = AppResult.Success(defaultHomeOverview()),
    private val membersResult: AppResult<List<CircleMember>> = AppResult.Success(emptyList()),
) : HomeRepository {
    var lastCall: HomeRepositoryCall? = null

    override suspend fun getHome(circleId: String): AppResult<HomeOverview> {
        lastCall = HomeRepositoryCall.GetHome(circleId = circleId)
        return homeResult
    }

    override suspend fun getMembers(circleId: String): AppResult<List<CircleMember>> {
        lastCall = HomeRepositoryCall.GetMembers(circleId = circleId)
        return membersResult
    }
}

private fun defaultHomeOverview(): HomeOverview = HomeOverview(
    circle = CircleSummary(
        circleId = "circle-1",
        name = "유니콘 스터디",
        ownerId = "owner-1",
    ),
    members = listOf(
        CircleMember(
            memberId = "member-1",
            nickname = "닉네임",
            avatarColor = "#FFFFFF",
            profileImageUrl = null,
            isMe = true,
            role = "MEMBER",
        ),
    ),
    canInvite = true,
    mainSchedule = ScheduleSummary(
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
    ),
    schedules = emptyList(),
    totalScheduleCount = 1L,
)
