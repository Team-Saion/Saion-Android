package com.saion.core.model.member

enum class MemberStatus(val value: String) {
    ACTIVE("ACTIVE"),
    DELETED("DELETED"),
    ;

    companion object {
        fun from(value: String): MemberStatus? = when (value.trim().uppercase()) {
            ACTIVE.value -> ACTIVE
            DELETED.value -> DELETED
            else -> null
        }
    }
}
