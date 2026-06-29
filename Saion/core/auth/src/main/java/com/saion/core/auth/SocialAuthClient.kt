package com.saion.core.auth

import android.content.Context

interface SocialAuthClient {
    suspend fun login(
        provider: SocialAuthProvider,
        context: Context,
    ): SocialLoginResult

    suspend fun logout(provider: SocialAuthProvider): SocialAuthActionResult
}
