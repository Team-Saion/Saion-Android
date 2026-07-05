package com.saion.core.model.member

enum class MemberRole(val value: String) {
    PENDING("PENDING"),
    MEMBER("MEMBER"),
    ADMIN("ADMIN"),
    ;

    companion object {
        fun from(value: String): MemberRole? = when (value.trim().uppercase()) {
            PENDING.value -> PENDING
            MEMBER.value -> MEMBER
            ADMIN.value -> ADMIN
            else -> null
        }
    }
}
