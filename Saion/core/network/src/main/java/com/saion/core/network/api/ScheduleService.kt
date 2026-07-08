package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.schedule.ConfirmationTypesListResponse
import com.saion.core.network.model.schedule.CreateScheduleRequest
import com.saion.core.network.model.schedule.RegisterConfirmationRequest
import com.saion.core.network.model.schedule.RegisterConfirmationResponse
import com.saion.core.network.model.schedule.ScheduleDetailResponse
import com.saion.core.network.model.schedule.ScheduleIdResponse
import com.saion.core.network.model.schedule.ScheduleListResponse
import com.saion.core.network.model.schedule.UpdateScheduleRequest
import com.saion.core.network.model.schedule.UpdateScheduleRequestValue
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Schedule API 원격 호출을 구성합니다.
 */
class ScheduleService(private val client: HttpClient) {
    suspend fun getScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): ApiResponse<ScheduleListResponse> = client
        .get("/api/v1/circles/$circleId/schedules") {
            cursor?.let { parameter("cursor", it) }
            size?.let { parameter("size", it) }
        }.toApiResponse()

    suspend fun createSchedule(
        circleId: String,
        request: CreateScheduleRequest,
    ): ApiResponse<ScheduleIdResponse> = client
        .post("/api/v1/circles/$circleId/schedules") {
            setBody(request)
        }.toApiResponse()

    suspend fun getScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<ScheduleDetailResponse> = client
        .get("/api/v1/circles/$circleId/schedules/$scheduleId")
        .toApiResponse()

    suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        request: UpdateScheduleRequest,
    ): ApiResponse<Unit> = client
        .patch("/api/v1/circles/$circleId/schedules/$scheduleId") {
            setBody(request.toRequestBody())
        }.toApiResponse()

    suspend fun deleteSchedule(
        circleId: String,
        scheduleId: String,
    ): ApiResponse<Unit> = client
        .delete("/api/v1/circles/$circleId/schedules/$scheduleId")
        .toApiResponse()

    suspend fun getConfirmationTypes(circleId: String): ApiResponse<ConfirmationTypesListResponse> = client
        .get("/api/v1/circles/$circleId/schedules/confirmations")
        .toApiResponse()

    suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: String,
    ): ApiResponse<RegisterConfirmationResponse> = client
        .post("/api/v1/circles/$circleId/schedules/$scheduleId/confirmations") {
            setBody(RegisterConfirmationRequest(confirmationType = confirmationType))
        }.toApiResponse()

    suspend fun cancelConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): ApiResponse<Unit> = client
        .delete("/api/v1/circles/$circleId/schedules/$scheduleId/confirmations/$confirmationId")
        .toApiResponse()
}

private fun UpdateScheduleRequest.toRequestBody(): JsonObject = buildMap<String, kotlinx.serialization.json.JsonElement> {
    put("needConfirm", JsonPrimitive(needConfirm))
    title.appendTo(this, "title")
    startDate.appendTo(this, "startDate")
    endDate.appendTo(this, "endDate")
    startTime.appendTo(this, "startTime")
    endTime.appendTo(this, "endTime")
    memo.appendTo(this, "memo")
}.let(::JsonObject)

private fun UpdateScheduleRequestValue<String>.appendTo(
    target: MutableMap<String, kotlinx.serialization.json.JsonElement>,
    key: String,
) {
    when (this) {
        UpdateScheduleRequestValue.Unchanged -> Unit
        is UpdateScheduleRequestValue.Set -> target[key] = value?.let(::JsonPrimitive) ?: JsonNull
    }
}
