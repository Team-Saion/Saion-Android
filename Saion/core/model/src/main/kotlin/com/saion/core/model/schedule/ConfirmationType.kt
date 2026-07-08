package com.saion.core.model.schedule

enum class ConfirmationType(val value: String) {
    CONFIRMED("CONFIRMED"),
    ETC("ETC"),
    ;

    companion object {
        fun from(value: String): ConfirmationType? = when (value.trim().uppercase()) {
            CONFIRMED.value -> CONFIRMED
            ETC.value -> ETC
            else -> null
        }
    }
}
