package com.saion.core.model.push

enum class PushPlatform(val value: String) {
    IOS("IOS"),
    ANDROID("ANDROID"),
    ;

    companion object {
        fun from(value: String): PushPlatform? = entries.firstOrNull { it.value == value }
    }
}
