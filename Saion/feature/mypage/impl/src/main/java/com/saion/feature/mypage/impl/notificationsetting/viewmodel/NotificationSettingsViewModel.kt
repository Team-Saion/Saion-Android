package com.saion.feature.mypage.impl.notificationsetting.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.notification.GetNotificationSettingUseCase
import com.saion.core.domain.usecase.notification.UpdateNotificationSettingUseCase
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.mypage.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
@Stable
internal class NotificationSettingsViewModel @Inject constructor(
    private val getNotificationSettingUseCase: GetNotificationSettingUseCase,
    private val updateNotificationSettingUseCase: UpdateNotificationSettingUseCase,
) : BaseViewModel<NotificationSettingsState, NotificationSettingsEffect, NotificationSettingsIntent>(
    NotificationSettingsState(),
) {
    private val pendingSettingUpdates = MutableSharedFlow<NotificationSetting>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private var lastSyncedSetting: NotificationSetting? = null

    init {
        observeSettingUpdates()
        loadSetting()
    }

    override fun handleIntent(intent: NotificationSettingsIntent) {
        when (intent) {
            is NotificationSettingsIntent.UpdateSetting -> updateSetting(intent.setting)
        }
    }

    private fun observeSettingUpdates() {
        viewModelScope.launch {
            pendingSettingUpdates
                .debounce(300)
                .filter { setting -> setting != lastSyncedSetting }
                .collect { setting ->
                    persistSetting(setting)
                }
        }
    }

    private fun loadSetting() {
        launchSafely(
            onStart = {
                update {
                    copy(
                        isLoading = true,
                        isLoadFailed = false,
                    )
                }
            },
            onSuccess = { setting ->
                lastSyncedSetting = setting
                update {
                    copy(
                        setting = setting,
                        isLoading = false,
                        isLoadFailed = false,
                    )
                }
            },
            onFailure = { error ->
                update {
                    copy(
                        isLoading = false,
                        isLoadFailed = setting == null,
                    )
                }
                emitEffect(
                    NotificationSettingsEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.notification_settings_error_load,
                            textMessage = { value, resId ->
                                NotificationSettingsSnackbarMessage.Text(value, resId)
                            },
                            errorMessage = { appError, resId ->
                                NotificationSettingsSnackbarMessage.Error(appError, resId)
                            },
                        ),
                    ),
                )
            },
        ) {
            getNotificationSettingUseCase()
        }
    }

    private fun updateSetting(setting: NotificationSetting) {
        if (currentState.setting == null || currentState.setting == setting) return

        update {
            copy(
                setting = setting,
                isLoadFailed = false,
            )
        }
        pendingSettingUpdates.tryEmit(setting)
    }

    private suspend fun persistSetting(setting: NotificationSetting) {
        launchSafely(
            onStart = {
                update { copy(isSaving = true) }
            },
            onSuccess = { savedSetting ->
                lastSyncedSetting = savedSetting
                update {
                    copy(
                        setting = savedSetting,
                        isSaving = false,
                        isLoadFailed = false,
                    )
                }
            },
            onFailure = { error ->
                val fallbackSetting = lastSyncedSetting
                update {
                    copy(
                        setting = fallbackSetting,
                        isSaving = false,
                        isLoadFailed = fallbackSetting == null,
                    )
                }
                emitEffect(
                    NotificationSettingsEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.notification_settings_error_save,
                            textMessage = { value, resId ->
                                NotificationSettingsSnackbarMessage.Text(value, resId)
                            },
                            errorMessage = { appError, resId ->
                                NotificationSettingsSnackbarMessage.Error(appError, resId)
                            },
                        ),
                    ),
                )
            },
        ) {
            updateNotificationSettingUseCase(setting)
        }.join()
    }
}
