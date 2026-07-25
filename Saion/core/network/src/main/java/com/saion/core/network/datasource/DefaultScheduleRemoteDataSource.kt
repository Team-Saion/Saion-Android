package com.saion.core.network.datasource

import com.saion.core.network.api.ScheduleService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.schedule.ConfirmationTypesListResponse
import com.saion.core.network.model.schedule.CreateScheduleRequest
import com.saion.core.network.model.schedule.RegisterConfirmationResponse
import com.saion.core.network.model.schedule.ScheduleDetailResponse
import com.saion.core.network.model.schedule.ScheduleIdResponse
import com.saion.core.network.model.schedule.ScheduleListResponse
import com.saion.core.network.model.schedule.UpdateScheduleRequest
import javax.inject.Inject

class DefaultScheduleRemoteDataSource @Inject constructor(
    private val service: ScheduleService,
) : ScheduleRemoteDataSource {
    override suspend fun getScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): ApiResponse<ScheduleListResponse> = service.getScheduleList(
        circleId = circleId,
        cursor = cursor,
        size = size,
    )

    override suspend fun createSchedule(
        circleId: String,
        request: CreateScheduleRequest,
    ): ApiResponse<ScheduleIdResponse> = service.createSchedule(
        circleId = circleId,
        request = request,
    )

    override suspend fun getScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<ScheduleDetailResponse> = service.getScheduleDetail(
        circleId = circleId,
        scheduleId = scheduleId,
    )

    override suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        request: UpdateScheduleRequest,
    ): ApiResponse<Unit> = service.updateSchedule(
        circleId = circleId,
        scheduleId = scheduleId,
        request = request,
    )

    override suspend fun deleteSchedule(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<Unit> = service.deleteSchedule(
        circleId = circleId,
        scheduleId = scheduleId,
    )

    override suspend fun getConfirmationTypes(circleId: String): ApiResponse<ConfirmationTypesListResponse> =
        service.getConfirmationTypes(circleId = circleId)

    override suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: String,
    ): ApiResponse<RegisterConfirmationResponse> = service.registerConfirmation(
        circleId = circleId,
        scheduleId = scheduleId,
        confirmationType = confirmationType,
    )

    override suspend fun cancelConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): ApiResponse<Unit> = service.cancelConfirmation(
        circleId = circleId,
        scheduleId = scheduleId,
        confirmationId = confirmationId,
    )

    override suspend fun requestFamilyNotification(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<Unit> = service.requestFamilyNotification(
        circleId = circleId,
        scheduleId = scheduleId,
    )
}
