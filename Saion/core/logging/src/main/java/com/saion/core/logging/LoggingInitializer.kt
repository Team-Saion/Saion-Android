package com.saion.core.logging

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import timber.log.Timber

private const val DEFAULT_TAG = "SAION_LOGGER"

/**
 * 앱 전역 Timber 설정을 초기화한다.
 *
 * 디버그 빌드에서는 [DebugTree]를, 운영 빌드에서는 [ReleaseTree]를 등록한다.
 * 기존 Tree가 이미 등록되어 있으면 모두 제거한 뒤 현재 빌드 타입에 맞는 정책만 다시 적용한다.
 */
fun initializeLogging(isDebug: Boolean) {
    configureLogger(isDebug = isDebug)

    Timber.uprootAll()
    Timber.plant(if (isDebug) DebugTree else ReleaseTree)
}

private fun configureLogger(isDebug: Boolean) {
    Logger.clearLogAdapters()
    Logger.addLogAdapter(
        AndroidLogAdapter(
            PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(if (isDebug) 1 else 0)
                .tag(DEFAULT_TAG)
                .build(),
        ),
    )
}
