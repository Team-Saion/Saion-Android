package com.saion.app

import android.app.Application
import com.saion.core.auth.AuthSdkInitializer
import com.saion.core.logging.initializeLogging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SaionApplication : Application() {
    @Inject
    lateinit var authSdkInitializer: AuthSdkInitializer

    override fun onCreate() {
        super.onCreate()
        authSdkInitializer.initialize()
        initializeLogging(isDebug = BuildConfig.DEBUG)
    }
}
