package com.saion.core.share.internal

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.share.BuildConfig
import com.saion.core.share.InvitationShareClient
import com.saion.core.share.InvitationShareResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine

internal class KakaoInvitationShareClient @Inject constructor(@param:ApplicationContext private val context: Context) : InvitationShareClient {
    override suspend fun shareInvitation(
        inviterName: String,
        circleName: String,
        invitation: IssuedInvitation,
    ): InvitationShareResult = runCatching {
        val templateArgs = mapOf(
            "inviterName" to inviterName,
            "circleName" to circleName,
            "inviteLink" to invitation.toDeepLink(),
            "inviteToken" to invitation.token,
            "inviteCode" to invitation.token,
            "expiresAt" to invitation.toDisplayExpiresAt(),
        )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            shareWithKakaoTalk(templateArgs)
        } else {
            shareWithBrowser(templateArgs)
        }

        InvitationShareResult.Success
    }.getOrElse { throwable ->
        InvitationShareResult.Failure(throwable.message)
    }

    private suspend fun shareWithKakaoTalk(templateArgs: Map<String, String>) {
        suspendCancellableCoroutine { continuation ->
            ShareClient.instance.shareCustom(
                context = context,
                templateId = BuildConfig.KAKAO_INVITATION_TEMPLATE_ID,
                templateArgs = templateArgs,
            ) { sharingResult, error ->
                when {
                    error != null -> continuation.resumeWith(Result.failure(error))

                    sharingResult == null -> continuation.resumeWith(
                        Result.failure(IllegalStateException("Kakao share result was null.")),
                    )

                    else -> {
                        context.startActivity(
                            sharingResult.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        )
                        continuation.resumeWith(Result.success(Unit))
                    }
                }
            }
        }
    }

    private fun shareWithBrowser(templateArgs: Map<String, String>) {
        val sharerUrl = WebSharerClient.instance.makeCustomUrl(
            templateId = BuildConfig.KAKAO_INVITATION_TEMPLATE_ID,
            templateArgs = templateArgs,
        )

        try {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
        } catch (_: UnsupportedOperationException) {
            try {
                KakaoCustomTabsClient.open(context, sharerUrl)
            } catch (exception: ActivityNotFoundException) {
                throw IllegalStateException("No browser available for Kakao share.", exception)
            }
        }
    }

    private fun IssuedInvitation.toDeepLink(): String = "kakao${BuildConfig.KAKAO_NATIVE_APP_KEY}://kakaolink?action=invite&token=$token"

    private fun IssuedInvitation.toDisplayExpiresAt(): String = runCatching {
        LocalDateTime.parse(expiresAt).format(EXPIRES_AT_FORMATTER)
    }.getOrElse { expiresAt }
}

private val EXPIRES_AT_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
