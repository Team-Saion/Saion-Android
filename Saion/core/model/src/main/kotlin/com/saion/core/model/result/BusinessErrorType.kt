package com.saion.core.model.result

enum class BusinessErrorType(private val codes: Set<String>) {
    INVALID_INPUT(
        codes = setOf("G400"),
    ),
    DUPLICATE(
        codes = setOf("M409_1"),
    ),
    NOT_FOUND(
        codes = setOf("G404", "M404_1", "M410_1", "M410_2"),
    ),
    ;

    fun matches(code: String): Boolean = code in codes

    companion object {
        fun from(code: String): BusinessErrorType? = entries.firstOrNull { it.matches(code) }
    }
}
