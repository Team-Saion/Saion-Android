package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.HomeRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUrgencyLevel
import com.saion.core.network.datasource.HomeRemoteDataSource
import com.saion.core.network.model.home.CircleHomeResponse
import com.saion.core.network.model.home.CircleMemberResponse
import com.saion.core.network.model.schedule.ScheduleSummaryResponse
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class HomeRepositoryImpl @Inject constructor(
    private val homeRemoteDataSource: HomeRemoteDataSource,
) : HomeRepository {
    private val homeCache = MutableStateFlow<Map<String, HomeOverview>>(emptyMap())

    override fun observeHome(circleId: String): Flow<HomeOverview?> = homeCache.map { it[circleId] }

    override fun observeMembers(circleId: String): Flow<List<CircleMember>> = homeCache
        .map { it[circleId]?.members.orEmpty() }
        .distinctUntilChanged()

    override suspend fun getHome(circleId: String): AppResult<HomeOverview> = homeCache.value[circleId]
        ?.let { AppResult.Success(it) }
        ?: refreshHome(circleId)

    override suspend fun refreshHome(circleId: String): AppResult<HomeOverview> = safeRequest(
        request = { homeRemoteDataSource.getHome(circleId = circleId) },
    ) { response ->
        when (val domain = response.toDomain()) {
            is AppResult.Success -> {
                cacheHome(circleId = circleId, overview = domain.data)
                domain
            }

            is AppResult.Failure -> domain
        }
    }

    override suspend fun getMembers(circleId: String): AppResult<List<CircleMember>> = homeCache.value[circleId]
        ?.let { AppResult.Success(it.members) }
        ?: safeRequest(
        request = { homeRemoteDataSource.getMembers(circleId = circleId) },
    ) { response ->
        val members = response.map(CircleMemberResponse::toDomain)
        val cachedOverview = homeCache.value[circleId]
        if (cachedOverview != null) {
            cacheHome(
                circleId = circleId,
                overview = cachedOverview.copy(members = members),
            )
        }
        AppResult.Success(members)
    }

    override suspend fun updateCachedMyMemberProfile(
        circleId: String,
        memberInfo: MemberInfo,
    ) {
        val cachedOverview = homeCache.value[circleId] ?: return
        val updatedMembers = cachedOverview.members.map { member ->
            if (member.isMe && member.memberId == memberInfo.memberId) {
                member.copy(
                    nickname = memberInfo.nickname,
                    avatarColor = memberInfo.avatarColorHex,
                    profileImageUrl = memberInfo.profileImageUrl,
                )
            } else {
                member
            }
        }
        cacheHome(
            circleId = circleId,
            overview = cachedOverview.copy(members = updatedMembers),
        )
    }

    private fun cacheHome(
        circleId: String,
        overview: HomeOverview,
    ) {
        homeCache.value = homeCache.value + (circleId to overview)
    }
}

private fun CircleHomeResponse.toDomain(): AppResult<HomeOverview> {
    val mainScheduleResponse = mainSchedule
    val mappedMainSchedule = mainScheduleResponse?.toDomain()
        ?: if (mainScheduleResponse != null) return mainScheduleResponse.invalidStatusResult() else null
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
    avatarColor = avatarColor.hex,
    profileImageUrl = profileImageUrl,
    isMe = isMe,
    role = role,
)

private fun ScheduleSummaryResponse.toDomain(): ScheduleSummary? = ScheduleStatus.from(status)?.let { scheduleStatus ->
    val mappedUrgencyLevel = ScheduleUrgencyLevel.from(urgencyLevel) ?: return null
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
        urgencyLevel = mappedUrgencyLevel,
        progressRate = progressRate,
        dday = dday ?: dDay,
    )
}

private fun ScheduleSummaryResponse.invalidStatusResult(): AppResult.Failure = AppResult.Failure(
    AppError.Unknown(message = "Schedule status is missing or invalid."),
)
