package com.saion.core.data.repository

import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.MyConfirmation
import com.saion.core.network.datasource.ScheduleRemoteDataSource
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.schedule.ConfirmationCountResponse
import com.saion.core.network.model.schedule.ConfirmationTypesListResponse
import com.saion.core.network.model.schedule.CreateScheduleRequest
import com.saion.core.network.model.schedule.RegisterConfirmationResponse
import com.saion.core.network.model.schedule.ScheduleDetailResponse
import com.saion.core.network.model.schedule.ScheduleIdResponse
import com.saion.core.network.model.schedule.ScheduleListResponse
import com.saion.core.network.model.schedule.ScheduleSummaryResponse
import com.saion.core.network.model.schedule.UpdateScheduleRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleRepositoryImplTest {
    @Test
    fun `확인 응답 등록 후 상세 캐시를 즉시 갱신한다`() = runTest {
        val remoteDataSource = FakeScheduleRemoteDataSource()
        val repository = ScheduleRepositoryImpl(remoteDataSource)

        repository.refreshScheduleDetail(circleId = "circle-1", scheduleId = "schedule-1")
        val initialDetail = repository.observeScheduleDetail(circleId = "circle-1", scheduleId = "schedule-1").first()
        assertEquals(null, initialDetail?.myConfirmation)

        val result = repository.registerConfirmation(
            circleId = "circle-1",
            scheduleId = "schedule-1",
            confirmationType = ConfirmationType.CONFIRMED,
        )

        assertTrue(result is AppResult.Success)
        val updatedDetail = repository.observeScheduleDetail(circleId = "circle-1", scheduleId = "schedule-1").first()
        assertEquals(
            MyConfirmation(
                confirmationId = 11L,
                confirmationType = ConfirmationType.CONFIRMED,
            ),
            updatedDetail?.myConfirmation,
        )
        assertEquals(2, remoteDataSource.detailRequestCount)
    }
}

private class FakeScheduleRemoteDataSource : ScheduleRemoteDataSource {
    var detailRequestCount: Int = 0
    private var hasConfirmation: Boolean = false

    override suspend fun getScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): ApiResponse<ScheduleListResponse> = ApiResponse(
        statusCode = 200,
        isSuccess = true,
        data = ScheduleListResponse(
            schedules = emptyList<ScheduleSummaryResponse>(),
            nextCursor = null,
            hasNext = false,
        ),
        errorCode = null,
        message = null,
        timestamp = "2026-07-18T00:00:00",
    )

    override suspend fun createSchedule(
        circleId: String,
        request: CreateScheduleRequest,
    ): ApiResponse<ScheduleIdResponse> = error("Not used")

    override suspend fun getScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<ScheduleDetailResponse> {
        detailRequestCount += 1
        return ApiResponse(
            statusCode = 200,
            isSuccess = true,
            data = ScheduleDetailResponse(
                scheduleId = scheduleId,
                title = "여행",
                startDate = "2026-07-18",
                endDate = "2026-07-18",
                startTime = "09:00",
                endTime = "10:00",
                isAllDay = false,
                needConfirm = true,
                status = "UPCOMING",
                urgencyLevel = "NORMAL",
                progressRate = 0,
                memo = null,
                confirmations = listOf(
                    ConfirmationCountResponse(
                        type = "CONFIRMED",
                        count = if (hasConfirmation) 1 else 0,
                    ),
                ),
                myConfirmation = if (hasConfirmation) {
                    com.saion.core.network.model.schedule.MyConfirmationResponse(
                        confirmationId = 11L,
                        confirmationType = "CONFIRMED",
                    )
                } else {
                    null
                },
                createdBy = "member-1",
                createdAt = "2026-07-18T00:00:00",
                dDay = 0,
            ),
            errorCode = null,
            message = null,
            timestamp = "2026-07-18T00:00:00",
        )
    }

    override suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        request: UpdateScheduleRequest,
    ): ApiResponse<Unit> = error("Not used")

    override suspend fun deleteSchedule(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<Unit> = error("Not used")

    override suspend fun getConfirmationTypes(circleId: String): ApiResponse<ConfirmationTypesListResponse> = error("Not used")

    override suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: String,
    ): ApiResponse<RegisterConfirmationResponse> {
        hasConfirmation = true
        return ApiResponse(
            statusCode = 200,
            isSuccess = true,
            data = RegisterConfirmationResponse(confirmationType = confirmationType),
            errorCode = null,
            message = null,
            timestamp = "2026-07-18T00:00:00",
        )
    }

    override suspend fun cancelConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): ApiResponse<Unit> = error("Not used")
}
