package com.saion.core.auth

enum class SocialLoginFailureReason {
    PROVIDER_UNAVAILABLE,
    MISSING_ID_TOKEN,
    SDK_ERROR,
    UNSUPPORTED_PROVIDER,
}

sealed interface SocialLoginResult {
    data class Success(
        val provider: SocialAuthProvider,
        val idToken: String,
        val accessToken: String? = null,
    ) : SocialLoginResult

    data object Cancelled : SocialLoginResult

    data class Failure(
        val reason: SocialLoginFailureReason,
        val message: String? = null,
    ) : SocialLoginResult
}

sealed interface SocialAuthActionResult {
    data object Success : SocialAuthActionResult

    data class Failure(val message: String? = null) : SocialAuthActionResult
}
