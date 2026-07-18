package com.saion.feature.mypage.impl.notificationsetting.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.notification.GetNotificationSettingUseCase
import com.saion.core.domain.usecase.notification.UpdateNotificationSettingUseCase
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.notification.NotificationLifecycleManager
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
    private val notificationLifecycleManager: NotificationLifecycleManager,
) : BaseViewModel<NotificationSettingsState, NotificationSettingsEffect, NotificationSettingsIntent>(
    NotificationSettingsState(),
) {
    private val pendingSettingUpdates = MutableSharedFlow<PendingSettingUpdate>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private var lastSyncedSetting: NotificationSetting? = null
    private var pendingPermissionSetting: NotificationSetting? = null

    init {
        observeSettingUpdates()
        loadSetting()
    }

    override fun handleIntent(intent: NotificationSettingsIntent) {
        when (intent) {
            is NotificationSettingsIntent.NotificationPermissionResolved -> handleNotificationPermissionResolved(
                granted = intent.granted,
                canRequestAgain = intent.canRequestAgain,
            )
            is NotificationSettingsIntent.UpdateSetting -> updateSetting(
                setting = intent.setting,
                osPermissionGranted = intent.osPermissionGranted,
            )
        }
    }

    private fun observeSettingUpdates() {
        viewModelScope.launch {
            pendingSettingUpdates
                .debounce(300)
                .filter { update -> update.setting != lastSyncedSetting }
                .collect { update ->
                    persistSetting(update)
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

    private fun updateSetting(
        setting: NotificationSetting,
        osPermissionGranted: Boolean,
    ) {
        val currentSetting = currentState.setting ?: return
        if (currentSetting == setting) return
        if (currentSetting.requiresNotificationPermissionFor(setting) && !osPermissionGranted) {
            pendingPermissionSetting = setting
            viewModelScope.launch {
                emitEffect(NotificationSettingsEffect.RequestNotificationPermission)
            }
            return
        }

        queueSettingUpdate(
            setting = setting,
            syncPermissionStateAfterSuccess = false,
        )
    }

    private fun handleNotificationPermissionResolved(
        granted: Boolean,
        canRequestAgain: Boolean,
    ) {
        val setting = pendingPermissionSetting ?: return
        pendingPermissionSetting = null

        if (!granted) {
            viewModelScope.launch {
                emitEffect(
                    NotificationSettingsEffect.ShowSnackbar(
                        if (canRequestAgain) {
                            NotificationSettingsSnackbarMessage.Text(
                                value = "",
                                defaultMessageResId = R.string.notification_settings_error_permission_required,
                            )
                        } else {
                            NotificationSettingsSnackbarMessage.PermissionPermanentlyDenied(
                                defaultMessageResId = R.string.notification_settings_error_permission_permanently_denied,
                            )
                        },
                    ),
                )
            }
            return
        }

        queueSettingUpdate(
            setting = setting,
            syncPermissionStateAfterSuccess = true,
        )
    }

    private fun queueSettingUpdate(
        setting: NotificationSetting,
        syncPermissionStateAfterSuccess: Boolean,
    ) {
        update {
            copy(
                setting = setting,
                isLoadFailed = false,
            )
        }
        pendingSettingUpdates.tryEmit(
            PendingSettingUpdate(
                setting = setting,
                syncPermissionStateAfterSuccess = syncPermissionStateAfterSuccess,
            ),
        )
    }

    private suspend fun persistSetting(updateRequest: PendingSettingUpdate) {
        launchSafely(
            onStart = {
                update { copy(isSaving = true) }
            },
            onSuccess = { savedSetting ->
                lastSyncedSetting = savedSetting
                if (updateRequest.syncPermissionStateAfterSuccess) {
                    viewModelScope.launch {
                        notificationLifecycleManager.syncOnNotificationPermissionGranted()
                    }
                }
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
            updateNotificationSettingUseCase(updateRequest.setting)
        }.join()
    }

    private data class PendingSettingUpdate(
        val setting: NotificationSetting,
        val syncPermissionStateAfterSuccess: Boolean,
    )
}

private fun NotificationSetting.requiresNotificationPermissionFor(updated: NotificationSetting): Boolean =
    !d7Enabled && updated.d7Enabled ||
        !d1Enabled && updated.d1Enabled ||
        !ddayEnabled && updated.ddayEnabled ||
        !familyScheduleCheckEnabled && updated.familyScheduleCheckEnabled
