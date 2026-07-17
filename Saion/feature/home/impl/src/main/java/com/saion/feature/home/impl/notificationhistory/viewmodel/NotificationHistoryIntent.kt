package com.saion.feature.home.impl.notificationhistory.viewmodel

import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface NotificationHistoryIntent : UIIntent {
    data class NotificationClicked(val item: NotificationInboxItem) : NotificationHistoryIntent
}
