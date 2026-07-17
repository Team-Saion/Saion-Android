package com.saion.feature.mypage.impl.notificationsetting.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.ui.viewmodel.UIState

@Immutable
internal data class NotificationSettingsState(
    val setting: NotificationSetting? = null,
    val isLoading: Boolean = true,
    val isLoadFailed: Boolean = false,
    val isSaving: Boolean = false,
) : UIState
