package com.saion.core.network.client

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AppPackageInfoProvider @Inject constructor(@param:ApplicationContext private val context: Context) {
    fun versionName(): String = runCatching {
        packageInfo().versionName.orEmpty().ifBlank { UNKNOWN_VERSION_NAME }
    }.getOrDefault(UNKNOWN_VERSION_NAME)

    fun versionCode(): Long = runCatching {
        packageInfo().longVersionCode
    }.getOrDefault(UNKNOWN_VERSION_CODE)

    private fun packageInfo() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.PackageInfoFlags.of(0),
        )
    } else {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }

    private companion object {
        const val UNKNOWN_VERSION_NAME = "unknown"
        const val UNKNOWN_VERSION_CODE = 0L
    }
}
