package com.saion.core.model.schedule

enum class ScheduleStatus(val value: String) {
    UPCOMING("UPCOMING"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    ;

    companion object {
        fun from(value: String): ScheduleStatus? = when (value.trim().uppercase()) {
            UPCOMING.value -> UPCOMING
            IN_PROGRESS.value -> IN_PROGRESS
            COMPLETED.value -> COMPLETED
            else -> null
        }
    }
}
