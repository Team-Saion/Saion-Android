package com.saion.feature.home.impl.notificationhistory.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.ui.viewmodel.UIState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class NotificationHistoryState(
    val items: ImmutableList<NotificationInboxItem> = persistentListOf(),
    val isLoading: Boolean = false,
    val isLoadFailed: Boolean = false,
) : UIState
