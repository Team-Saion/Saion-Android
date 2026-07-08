package com.saion.core.model.schedule

sealed interface ScheduleUpdateValue<out T> {
    data object Unchanged : ScheduleUpdateValue<Nothing>

    data class Set<T>(val value: T?) : ScheduleUpdateValue<T>
}
