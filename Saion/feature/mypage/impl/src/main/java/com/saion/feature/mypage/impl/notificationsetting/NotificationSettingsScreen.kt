package com.saion.feature.mypage.impl.notificationsetting

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.theme.SaionTheme
import com.saion.feature.mypage.impl.R
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsEffect
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsIntent
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsSnackbarMessage
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsState
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsViewModel

@Composable
internal fun NotificationSettingsScreen(
    onBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is NotificationSettingsEffect.ShowSnackbar -> {
                snackbarHostState.showSnackbar(effect.message.resolve(context))
            }
        }
    }

    NotificationSettingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onSettingChanged = { setting ->
            viewModel.dispatch(NotificationSettingsIntent.UpdateSetting(setting))
        },
    )
}

@Composable
private fun NotificationSettingsScreen(
    uiState: NotificationSettingsState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onSettingChanged: (NotificationSetting) -> Unit,
) {
    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                modifier = Modifier.statusBarsPadding(),
                variant = TopBarVariant.Standard(
                    title = stringResource(R.string.notification_settings_title),
                    onBack = onBack,
                ),
            )
        },
    ) {
        when {
            uiState.isLoading -> SaionSpinner()

            uiState.setting == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.notification_settings_error_load),
                        style = SaionTheme.typography.body1,
                        color = SaionTheme.colors.label.subtle,
                    )
                }
            }

            else -> {
                NotificationSettingsContent(
                    setting = uiState.setting,
                    onSettingChanged = onSettingChanged,
                )
            }
        }
    }
}

@Composable
private fun NotificationSettingsContent(
    setting: NotificationSetting,
    onSettingChanged: (NotificationSetting) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        NotificationToggleRow(
            title = stringResource(R.string.notification_settings_d7),
            checked = setting.d7Enabled,
            onCheckedChange = { checked ->
                onSettingChanged(setting.copy(d7Enabled = checked))
            },
        )
        NotificationToggleRow(
            title = stringResource(R.string.notification_settings_d1),
            checked = setting.d1Enabled,
            onCheckedChange = { checked ->
                onSettingChanged(setting.copy(d1Enabled = checked))
            },
        )
        NotificationToggleRow(
            title = stringResource(R.string.notification_settings_dday),
            checked = setting.ddayEnabled,
            onCheckedChange = { checked ->
                onSettingChanged(setting.copy(ddayEnabled = checked))
            },
        )
        NotificationToggleRow(
            title = stringResource(R.string.notification_settings_family_schedule_check),
            checked = setting.familyScheduleCheckEnabled,
            onCheckedChange = { checked ->
                onSettingChanged(setting.copy(familyScheduleCheckEnabled = checked))
            },
        )
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = SaionTheme.typography.body1,
            color = SaionTheme.colors.label.default,
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SaionTheme.colors.background.default,
                checkedTrackColor = SaionTheme.colors.status.positive.default,
                checkedBorderColor = SaionTheme.colors.status.positive.default,
                uncheckedThumbColor = SaionTheme.colors.background.default,
                uncheckedTrackColor = SaionTheme.colors.fill.disabled,
                uncheckedBorderColor = SaionTheme.colors.fill.disabled,
            ),
            modifier = Modifier.height(28.dp)
        )
    }
}

private fun NotificationSettingsSnackbarMessage.resolve(context: Context): String = when (this) {
    is NotificationSettingsSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is NotificationSettingsSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

@Preview(showBackground = true)
@Composable
private fun NotificationSettingsScreenPreview() {
    SaionTheme {
        NotificationSettingsScreen(
            uiState = NotificationSettingsState(
                setting = NotificationSetting(
                    d7Enabled = true,
                    d1Enabled = true,
                    ddayEnabled = true,
                    familyScheduleCheckEnabled = true,
                ),
                isLoading = false,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onSettingChanged = {},
        )
    }
}
