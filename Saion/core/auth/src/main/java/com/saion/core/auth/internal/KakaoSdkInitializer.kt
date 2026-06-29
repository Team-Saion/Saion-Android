package com.saion.core.auth.internal

import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.saion.core.auth.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal interface ProviderSdkInitializer {
    fun initialize()
}

internal class KakaoSdkInitializer @Inject constructor(@param:ApplicationContext private val context: Context) : ProviderSdkInitializer {
    override fun initialize() {
        if (BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()) {
            KakaoSdk.init(context, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }
    }
}
