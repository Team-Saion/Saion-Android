package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationCount
import com.saion.core.model.schedule.ConfirmationOption
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.CreateScheduleCommand
import com.saion.core.model.schedule.CreatedSchedule
import com.saion.core.model.schedule.MyConfirmation
import com.saion.core.model.schedule.RegisteredConfirmation
import com.saion.core.model.schedule.ScheduleDetail
import com.saion.core.model.schedule.ScheduleListPage
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUrgencyLevel
import com.saion.core.model.schedule.ScheduleUpdateValue
import com.saion.core.model.schedule.UpdateScheduleCommand
import com.saion.core.network.datasource.ScheduleRemoteDataSource
import com.saion.core.network.model.schedule.ConfirmationCountResponse
import com.saion.core.network.model.schedule.ConfirmationTypesListResponse
import com.saion.core.network.model.schedule.ConfirmationTypesResponse
import com.saion.core.network.model.schedule.CreateScheduleRequest
import com.saion.core.network.model.schedule.MyConfirmationResponse
import com.saion.core.network.model.schedule.RegisterConfirmationResponse
import com.saion.core.network.model.schedule.ScheduleDetailResponse
import com.saion.core.network.model.schedule.ScheduleIdResponse
import com.saion.core.network.model.schedule.ScheduleListResponse
import com.saion.core.network.model.schedule.ScheduleSummaryResponse
import com.saion.core.network.model.schedule.UpdateScheduleRequest
import com.saion.core.network.model.schedule.UpdateScheduleRequestValue
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScheduleRepositoryImpl @Inject constructor(private val scheduleRemoteDataSource: ScheduleRemoteDataSource) : ScheduleRepository {
    private val cacheMutex = Mutex()
    private val scheduleListState = MutableStateFlow<Map<String, ScheduleListPage>>(emptyMap())
    private val scheduleDetailState = MutableStateFlow<Map<String, ScheduleDetail>>(emptyMap())

    override fun observeScheduleList(circleId: String): Flow<ScheduleListPage?> = scheduleListState.map { it[circleId] }.distinctUntilChanged()

    override fun observeScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): Flow<ScheduleDetail?> = scheduleDetailState.map { it[scheduleCacheKey(circleId, scheduleId)] }.distinctUntilChanged()

    override suspend fun getCachedScheduleList(circleId: String): ScheduleListPage? = cacheMutex.withLock {
        scheduleListState.value[circleId]
    }

    override suspend fun getScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): AppResult<ScheduleListPage> {
        if (cursor == null) {
            getCachedScheduleList(circleId)?.let { return AppResult.Success(it) }
        }
        return refreshScheduleList(circleId = circleId, cursor = cursor, size = size)
    }

    override suspend fun refreshScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): AppResult<ScheduleListPage> = safeRequest(
        request = { scheduleRemoteDataSource.getScheduleList(circleId = circleId, cursor = cursor, size = size) },
    ) { response ->
        when (val page = response.toDomain()) {
            is AppResult.Success -> {
                cacheScheduleList(circleId = circleId, page = page.data, append = cursor != null)
                page
            }

            is AppResult.Failure -> page
        }
    }

    override suspend fun createSchedule(
        circleId: String,
        command: CreateScheduleCommand,
    ): AppResult<CreatedSchedule> = safeRequest(
        request = {
            scheduleRemoteDataSource.createSchedule(
                circleId = circleId,
                request = CreateScheduleRequest(
                    title = command.title,
                    startDate = command.startDate,
                    endDate = command.endDate,
                    startTime = command.startTime,
                    endTime = command.endTime,
                    needConfirm = command.needConfirm,
                    memo = command.memo,
                ),
            )
        },
    ) { response ->
        val createdSchedule = response.toDomain()
        if (getCachedScheduleList(circleId) != null) {
            refreshScheduleList(circleId = circleId)
        }
        AppResult.Success(createdSchedule)
    }

    override suspend fun getCachedScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): ScheduleDetail? = cacheMutex.withLock {
        scheduleDetailState.value[scheduleCacheKey(circleId, scheduleId)]
    }

    override suspend fun getScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): AppResult<ScheduleDetail> = getCachedScheduleDetail(circleId, scheduleId)
        ?.let { AppResult.Success(it) }
        ?: refreshScheduleDetail(circleId, scheduleId)

    override suspend fun refreshScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): AppResult<ScheduleDetail> = safeRequest(
        request = { scheduleRemoteDataSource.getScheduleDetail(circleId = circleId, scheduleId = scheduleId) },
    ) { response ->
        when (val detail = response.toDomain()) {
            is AppResult.Success -> {
                cacheScheduleDetail(circleId = circleId, detail = detail.data)
                detail
            }

            is AppResult.Failure -> detail
        }
    }

    override suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        command: UpdateScheduleCommand,
    ): AppResult<Unit> = safeRequest(
        request = {
            scheduleRemoteDataSource.updateSchedule(
                circleId = circleId,
                scheduleId = scheduleId,
                request = command.toRequest(),
            )
        },
    ) {
        refreshScheduleDetail(circleId = circleId, scheduleId = scheduleId)
        if (containsScheduleInList(circleId = circleId, scheduleId = scheduleId)) {
            refreshScheduleList(circleId = circleId)
        }
        AppResult.Success(Unit)
    }

    override suspend fun deleteSchedule(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit> = safeRequest(
        request = { scheduleRemoteDataSource.deleteSchedule(circleId = circleId, scheduleId = scheduleId) },
    ) {
        removeScheduleFromCache(circleId = circleId, scheduleId = scheduleId)
        AppResult.Success(Unit)
    }

    override suspend fun getConfirmationTypes(circleId: String): AppResult<List<ConfirmationOption>> = safeRequest(
        request = { scheduleRemoteDataSource.getConfirmationTypes(circleId = circleId) },
    ) { response ->
        response.toDomain()
    }

    override suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: ConfirmationType,
    ): AppResult<RegisteredConfirmation> = safeRequest(
        request = {
            scheduleRemoteDataSource.registerConfirmation(
                circleId = circleId,
                scheduleId = scheduleId,
                confirmationType = confirmationType.value,
            )
        },
    ) { response ->
        when (val registeredConfirmation = response.toDomain()) {
            is AppResult.Failure -> registeredConfirmation
            is AppResult.Success -> {
                refreshScheduleDetail(circleId = circleId, scheduleId = scheduleId)
                if (containsScheduleInList(circleId = circleId, scheduleId = scheduleId)) {
                    refreshScheduleList(circleId = circleId)
                }
                registeredConfirmation
            }
        }
    }

    override suspend fun cancelConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): AppResult<Unit> = safeRequest(
        request = {
            scheduleRemoteDataSource.cancelConfirmation(
                circleId = circleId,
                scheduleId = scheduleId,
                confirmationId = confirmationId,
            )
        },
    ) {
        refreshScheduleDetail(circleId = circleId, scheduleId = scheduleId)
        if (containsScheduleInList(circleId = circleId, scheduleId = scheduleId)) {
            refreshScheduleList(circleId = circleId)
        }
        AppResult.Success(Unit)
    }

    override suspend fun requestFamilyNotification(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit> = safeRequest(
        request = {
            scheduleRemoteDataSource.requestFamilyNotification(
                circleId = circleId,
                scheduleId = scheduleId,
            )
        },
    ) {
        AppResult.Success(Unit)
    }

    private suspend fun cacheScheduleList(
        circleId: String,
        page: ScheduleListPage,
        append: Boolean,
    ) {
        cacheMutex.withLock {
            val updatedPage = if (append) {
                val cachedPage = scheduleListState.value[circleId]
                if (cachedPage == null) {
                    page
                } else {
                    val mergedSchedules = (cachedPage.schedules + page.schedules)
                        .distinctBy(ScheduleSummary::scheduleId)
                    cachedPage.copy(
                        schedules = mergedSchedules,
                        nextCursor = page.nextCursor,
                        hasNext = page.hasNext,
                    )
                }
            } else {
                page
            }
            scheduleListState.value = scheduleListState.value + (circleId to updatedPage)
        }
    }

    private suspend fun cacheScheduleDetail(
        circleId: String,
        detail: ScheduleDetail,
    ) {
        cacheMutex.withLock {
            scheduleDetailState.value = scheduleDetailState.value + (scheduleCacheKey(circleId, detail.scheduleId) to detail)
        }
    }

    private suspend fun containsScheduleInList(
        circleId: String,
        scheduleId: String,
    ): Boolean = cacheMutex.withLock {
        scheduleListState.value[circleId]?.schedules?.any { it.scheduleId == scheduleId } == true
    }

    private suspend fun removeScheduleFromCache(
        circleId: String,
        scheduleId: String,
    ) {
        cacheMutex.withLock {
            val cachedPage = scheduleListState.value[circleId]
            if (cachedPage != null) {
                scheduleListState.value = scheduleListState.value + (
                    circleId to cachedPage.copy(
                        schedules = cachedPage.schedules.filterNot { it.scheduleId == scheduleId },
                    )
                )
            }
            scheduleDetailState.value = scheduleDetailState.value - scheduleCacheKey(circleId, scheduleId)
        }
    }
}

private fun scheduleCacheKey(
    circleId: String,
    scheduleId: String,
): String = "$circleId::$scheduleId"

private fun ScheduleIdResponse.toDomain(): CreatedSchedule = CreatedSchedule(scheduleId = scheduleId)

private fun UpdateScheduleCommand.toRequest(): UpdateScheduleRequest = UpdateScheduleRequest(
    title = title.toRequestValue(),
    startDate = startDate.toRequestValue(),
    endDate = endDate.toRequestValue(),
    startTime = startTime.toRequestValue(),
    endTime = endTime.toRequestValue(),
    needConfirm = needConfirm,
    memo = memo.toRequestValue(),
)

private fun <T> ScheduleUpdateValue<T>.toRequestValue(): UpdateScheduleRequestValue<T> = when (this) {
    ScheduleUpdateValue.Unchanged -> UpdateScheduleRequestValue.Unchanged
    is ScheduleUpdateValue.Set -> UpdateScheduleRequestValue.Set(value)
}

private fun ScheduleListResponse.toDomain(): AppResult<ScheduleListPage> {
    val schedules = schedules.map { response -> response.toDomain() ?: return response.invalidStatusResult() }
    return AppResult.Success(
        ScheduleListPage(
            schedules = schedules,
            nextCursor = nextCursor,
            hasNext = hasNext,
        ),
    )
}

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

private fun ScheduleDetailResponse.toDomain(): AppResult<ScheduleDetail> {
    val scheduleStatus = ScheduleStatus.from(status)
        ?: return AppResult.Failure(AppError.Unknown(message = "Schedule status is missing or invalid."))
    val confirmationCounts = confirmations.map { response ->
        response.toDomain() ?: return response.invalidTypeResult()
    }
    val myConfirmationResponse = myConfirmation
    val myConfirmationModel = if (myConfirmationResponse == null) {
        null
    } else {
        myConfirmationResponse.toDomain() ?: return myConfirmationResponse.invalidTypeResult()
    }

    return AppResult.Success(
        ScheduleDetail(
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
            memo = memo,
            confirmations = confirmationCounts,
            myConfirmation = myConfirmationModel,
            createdBy = createdBy,
            createdAt = createdAt,
        ),
    )
}

private fun ConfirmationCountResponse.toDomain(): ConfirmationCount? = ConfirmationType.from(type)?.let { confirmationType ->
    ConfirmationCount(
        type = confirmationType,
        count = count,
    )
}

private fun ConfirmationCountResponse.invalidTypeResult(): AppResult.Failure = AppResult.Failure(
    AppError.Unknown(message = "Confirmation type is missing or invalid."),
)

private fun MyConfirmationResponse.toDomain(): MyConfirmation? = ConfirmationType.from(confirmationType)?.let { type ->
    MyConfirmation(
        confirmationId = confirmationId,
        confirmationType = type,
    )
}

private fun MyConfirmationResponse.invalidTypeResult(): AppResult.Failure = AppResult.Failure(
    AppError.Unknown(message = "Confirmation type is missing or invalid."),
)

private fun ConfirmationTypesListResponse.toDomain(): AppResult<List<ConfirmationOption>> {
    val options = confirmationTypes.map { response ->
        response.toDomain() ?: return response.invalidTypeResult()
    }
    return AppResult.Success(options)
}

private fun ConfirmationTypesResponse.toDomain(): ConfirmationOption? = ConfirmationType.from(value)?.let { type ->
    ConfirmationOption(
        value = type,
        label = label,
    )
}

private fun ConfirmationTypesResponse.invalidTypeResult(): AppResult.Failure = AppResult.Failure(
    AppError.Unknown(message = "Confirmation type is missing or invalid."),
)

private fun RegisterConfirmationResponse.toDomain(): AppResult<RegisteredConfirmation> {
    val type = ConfirmationType.from(confirmationType)
        ?: return AppResult.Failure(AppError.Unknown(message = "Confirmation type is missing or invalid."))
    return AppResult.Success(
        RegisteredConfirmation(confirmationType = type),
    )
}
