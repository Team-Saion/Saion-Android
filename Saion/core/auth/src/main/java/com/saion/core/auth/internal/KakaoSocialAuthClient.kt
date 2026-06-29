package com.saion.core.auth.internal

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.saion.core.auth.SocialAuthActionResult
import com.saion.core.auth.SocialAuthClient
import com.saion.core.auth.SocialAuthProvider
import com.saion.core.auth.SocialLoginFailureReason
import com.saion.core.auth.SocialLoginResult
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

internal class KakaoSocialAuthClient @Inject constructor() : SocialAuthClient {
    override suspend fun login(
        provider: SocialAuthProvider,
        context: Context,
    ): SocialLoginResult = when (provider) {
        SocialAuthProvider.KAKAO -> loginWithKakao(context)
    }

    override suspend fun logout(provider: SocialAuthProvider): SocialAuthActionResult = when (provider) {
        SocialAuthProvider.KAKAO -> logoutKakao()
    }

    private suspend fun loginWithKakao(context: Context): SocialLoginResult = suspendCancellableCoroutine { continuation ->
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (continuation.isActive) {
                continuation.resume(
                    when {
                        error == null && token?.idToken != null -> {
                            SocialLoginResult.Success(
                                provider = SocialAuthProvider.KAKAO,
                                idToken = token.idToken!!,
                                accessToken = token.accessToken,
                            )
                        }

                        error == null -> SocialLoginResult.Failure(
                            reason = SocialLoginFailureReason.MISSING_ID_TOKEN,
                        )

                        error.isKakaoLoginCancelled() -> SocialLoginResult.Cancelled

                        else -> SocialLoginResult.Failure(
                            reason = SocialLoginFailureReason.SDK_ERROR,
                            message = error.message,
                        )
                    },
                )
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context = context) { token, error ->
                when {
                    error == null -> callback(token, null)

                    error.isKakaoLoginCancelled() -> callback(null, error)

                    else -> {
                        UserApiClient.instance.loginWithKakaoAccount(
                            context = context,
                            callback = callback,
                        )
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                context = context,
                callback = callback,
            )
        }
    }

    private suspend fun logoutKakao(): SocialAuthActionResult = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.logout { error ->
            if (!continuation.isActive) return@logout

            continuation.resume(
                if (error == null) {
                    SocialAuthActionResult.Success
                } else {
                    SocialAuthActionResult.Failure(message = error.message)
                },
            )
        }
    }

    private fun Throwable?.isKakaoLoginCancelled(): Boolean = this is ClientError && reason == ClientErrorCause.Cancelled
}
