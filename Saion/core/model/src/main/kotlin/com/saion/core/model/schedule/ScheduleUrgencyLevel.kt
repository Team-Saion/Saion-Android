package com.saion.core.model.schedule

enum class ScheduleUrgencyLevel(val value: String) {
    URGENT("URGENT"),
    NORMAL("NORMAL"),
    ;

    companion object {
        fun from(value: String): ScheduleUrgencyLevel? = entries.firstOrNull { it.value == value }
    }
}
