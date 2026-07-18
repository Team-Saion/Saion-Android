package com.saion.core.notification

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationAppInfoProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun versionName(): String = runCatching {
        packageInfo().versionName.orEmpty().ifBlank { UNKNOWN_VERSION_NAME }
    }.getOrDefault(UNKNOWN_VERSION_NAME)

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
    }
}
