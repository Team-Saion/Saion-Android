package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.HomeRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.network.datasource.HomeRemoteDataSource
import com.saion.core.network.model.home.CircleHomeResponse
import com.saion.core.network.model.home.CircleMemberResponse
import com.saion.core.network.model.schedule.ScheduleSummaryResponse
import javax.inject.Inject

internal class HomeRepositoryImpl @Inject constructor(
    private val homeRemoteDataSource: HomeRemoteDataSource,
) : HomeRepository {
    override suspend fun getHome(circleId: String): AppResult<HomeOverview> = safeRequest(
        request = { homeRemoteDataSource.getHome(circleId = circleId) },
    ) { response ->
        response.toDomain()
    }

    override suspend fun getMembers(circleId: String): AppResult<List<CircleMember>> = safeRequest(
        request = { homeRemoteDataSource.getMembers(circleId = circleId) },
    ) { response ->
        AppResult.Success(response.map(CircleMemberResponse::toDomain))
    }
}

private fun CircleHomeResponse.toDomain(): AppResult<HomeOverview> {
    val mainScheduleResponse = mainSchedule
    val mappedMainSchedule = if (mainScheduleResponse == null) {
        null
    } else {
        mainScheduleResponse.toDomain() ?: return mainScheduleResponse.invalidStatusResult()
    }
    val mappedSchedules = schedules.map { response ->
        response.toDomain() ?: return response.invalidStatusResult()
    }

    return AppResult.Success(
        HomeOverview(
            circle = CircleSummary(
                circleId = circle.circleId,
                name = circle.name,
                ownerId = circle.ownerId,
            ),
            members = members.map(CircleMemberResponse::toDomain),
            canInvite = canInvite,
            mainSchedule = mappedMainSchedule,
            schedules = mappedSchedules,
            totalScheduleCount = totalScheduleCount,
        ),
    )
}

private fun CircleMemberResponse.toDomain(): CircleMember = CircleMember(
    memberId = memberId,
    nickname = nickname,
    avatarColor = avatarColor,
    isMe = isMe,
    role = role,
)

private fun ScheduleSummaryResponse.toDomain(): ScheduleSummary? = ScheduleStatus.from(status)?.let { scheduleStatus ->
    ScheduleSummary(
        scheduleId = scheduleId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        startTime = startTime,
        endTime = endTime,
        isAllDay = isAllDay,
        needConfirm = needConfirm,
        status = scheduleStatus,
        progressRate = progressRate,
        dday = dday ?: dDay,
    )
}

private fun ScheduleSummaryResponse.invalidStatusResult(): AppResult.Failure = AppResult.Failure(
    AppError.Unknown(message = "Schedule status is missing or invalid."),
)
