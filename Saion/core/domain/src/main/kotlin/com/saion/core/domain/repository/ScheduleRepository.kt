package com.saion.core.domain.repository

import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationOption
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.CreateScheduleCommand
import com.saion.core.model.schedule.CreatedSchedule
import com.saion.core.model.schedule.RegisteredConfirmation
import com.saion.core.model.schedule.ScheduleDetail
import com.saion.core.model.schedule.ScheduleListPage
import com.saion.core.model.schedule.UpdateScheduleCommand
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun observeScheduleList(circleId: String): Flow<ScheduleListPage?>

    fun observeScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): Flow<ScheduleDetail?>

    suspend fun getCachedScheduleList(circleId: String): ScheduleListPage?

    suspend fun getScheduleList(
        circleId: String,
        cursor: String? = null,
        size: Int? = null,
    ): AppResult<ScheduleListPage>

    suspend fun refreshScheduleList(
        circleId: String,
        cursor: String? = null,
        size: Int? = null,
    ): AppResult<ScheduleListPage>

    suspend fun createSchedule(
        circleId: String,
        command: CreateScheduleCommand,
    ): AppResult<CreatedSchedule>

    suspend fun getScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): AppResult<ScheduleDetail>

    suspend fun getCachedScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): ScheduleDetail?

    suspend fun refreshScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): AppResult<ScheduleDetail>

    suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        command: UpdateScheduleCommand,
    ): AppResult<Unit>

    suspend fun deleteSchedule(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit>

    suspend fun getConfirmationTypes(circleId: String): AppResult<List<ConfirmationOption>>

    suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: ConfirmationType,
    ): AppResult<RegisteredConfirmation>

    suspend fun cancelConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): AppResult<Unit>

    suspend fun requestFamilyNotification(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit>
}
