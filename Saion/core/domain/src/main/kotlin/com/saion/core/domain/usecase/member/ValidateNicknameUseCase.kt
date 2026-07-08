package com.saion.core.domain.usecase.member

import com.saion.core.model.member.NicknameValidation
import com.saion.core.model.member.NicknameValidationResult
import javax.inject.Inject

class ValidateNicknameUseCase @Inject constructor() {
    operator fun invoke(nickname: String): NicknameValidation {
        val trimmedNickname = nickname.trim()
        val result = when {
            trimmedNickname.isEmpty() -> NicknameValidationResult.Empty
            trimmedNickname.length < MIN_NICKNAME_LENGTH -> NicknameValidationResult.TooShort
            trimmedNickname.length > MAX_NICKNAME_LENGTH -> NicknameValidationResult.TooLong
            !ALLOWED_NICKNAME_REGEX.matches(nickname) -> NicknameValidationResult.InvalidCharacter
            else -> NicknameValidationResult.Valid
        }

        return NicknameValidation(
            originalNickname = nickname,
            trimmedNickname = trimmedNickname,
            result = result,
        )
    }

    companion object {
        private const val MIN_NICKNAME_LENGTH = 2
        private const val MAX_NICKNAME_LENGTH = 10
        private val ALLOWED_NICKNAME_REGEX = Regex("^[가-힣A-Za-z0-9 ]*$")
    }
}
