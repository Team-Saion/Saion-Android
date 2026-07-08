package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.schedule.ConfirmationTypesListResponse
import com.saion.core.network.model.schedule.CreateScheduleRequest
import com.saion.core.network.model.schedule.RegisterConfirmationResponse
import com.saion.core.network.model.schedule.ScheduleDetailResponse
import com.saion.core.network.model.schedule.ScheduleIdResponse
import com.saion.core.network.model.schedule.ScheduleListResponse
import com.saion.core.network.model.schedule.UpdateScheduleRequest

interface ScheduleRemoteDataSource {
    suspend fun getScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): ApiResponse<ScheduleListResponse>

    suspend fun createSchedule(
        circleId: String,
        request: CreateScheduleRequest,
    ): ApiResponse<ScheduleIdResponse>

    suspend fun getScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<ScheduleDetailResponse>

    suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        request: UpdateScheduleRequest,
    ): ApiResponse<Unit>

    suspend fun deleteSchedule(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<Unit>

    suspend fun getConfirmationTypes(circleId: String): ApiResponse<ConfirmationTypesListResponse>

    suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: String,
    ): ApiResponse<RegisterConfirmationResponse>

    suspend fun cancelConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): ApiResponse<Unit>
}
