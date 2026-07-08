package com.saion.core.network.model.schedule

sealed interface UpdateScheduleRequestValue<out T> {
    data object Unchanged : UpdateScheduleRequestValue<Nothing>

    data class Set<T>(val value: T?) : UpdateScheduleRequestValue<T>
}

data class UpdateScheduleRequest(
    val title: UpdateScheduleRequestValue<String> = UpdateScheduleRequestValue.Unchanged,
    val startDate: UpdateScheduleRequestValue<String> = UpdateScheduleRequestValue.Unchanged,
    val endDate: UpdateScheduleRequestValue<String> = UpdateScheduleRequestValue.Unchanged,
    val startTime: UpdateScheduleRequestValue<String> = UpdateScheduleRequestValue.Unchanged,
    val endTime: UpdateScheduleRequestValue<String> = UpdateScheduleRequestValue.Unchanged,
    val needConfirm: Boolean,
    val memo: UpdateScheduleRequestValue<String> = UpdateScheduleRequestValue.Unchanged,
)
