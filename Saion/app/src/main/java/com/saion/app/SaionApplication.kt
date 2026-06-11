package com.saion.app

import android.app.Application
import com.saion.core.logging.initializeLogging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SaionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeLogging(isDebug = BuildConfig.DEBUG)
    }
}
