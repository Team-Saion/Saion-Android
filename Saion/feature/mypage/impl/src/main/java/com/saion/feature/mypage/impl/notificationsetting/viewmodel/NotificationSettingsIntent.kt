package com.saion.feature.mypage.impl.notificationsetting.viewmodel

import com.saion.core.model.notification.NotificationSetting
import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface NotificationSettingsIntent : UIIntent {
    data class UpdateSetting(
        val setting: NotificationSetting,
        val osPermissionGranted: Boolean,
    ) : NotificationSettingsIntent

    data class NotificationPermissionResolved(
        val granted: Boolean,
        val canRequestAgain: Boolean = true,
    ) : NotificationSettingsIntent
}
