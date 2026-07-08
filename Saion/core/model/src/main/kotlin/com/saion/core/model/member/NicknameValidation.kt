package com.saion.core.model.member

data class NicknameValidation(
    val originalNickname: String,
    val trimmedNickname: String,
    val result: NicknameValidationResult,
) {
    val isValid: Boolean
        get() = result == NicknameValidationResult.Valid
}

sealed interface NicknameValidationResult {
    data object Empty : NicknameValidationResult

    data object TooShort : NicknameValidationResult

    data object TooLong : NicknameValidationResult

    data object InvalidCharacter : NicknameValidationResult

    data object Valid : NicknameValidationResult
}
