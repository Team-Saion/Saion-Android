package com.saion.core.logging

import android.util.Log
import com.orhanobut.logger.Logger
import timber.log.Timber

/**
 * 개발 빌드에서 모든 Timber 로그를 보기 쉬운 형태로 출력한다.
 */
internal object DebugTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        Logger.log(priority, tag?.uppercase(), message.resolveWith(t), t)
    }
}

/**
 * 운영 빌드에서 필요한 수준의 로그만 남긴다.
 *
 * 현재 단계에서는 `WARN` 이상만 출력 대상으로 유지한다.
 */
internal object ReleaseTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        if (!shouldLogInRelease(priority)) return

        Logger.log(priority, tag?.uppercase(), message.resolveWith(t), t)
    }
}

/**
 * 운영 빌드에서 보존할 로그 레벨인지 판단한다.
 */
internal fun shouldLogInRelease(priority: Int): Boolean = priority >= Log.WARN

/**
 * 메시지가 비어 있으면 예외 문자열을 대신 사용한다.
 */
internal fun String.resolveWith(throwable: Throwable?): String = if (isBlank() && throwable != null) throwable.stackTraceToString() else this
