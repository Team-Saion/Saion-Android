package com.saion.feature.mypage.impl.mypage

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.theme.SaionTheme
import com.saion.feature.mypage.impl.R
import com.saion.feature.mypage.impl.mypage.component.MyPageMenuCard
import com.saion.feature.mypage.impl.mypage.component.MyPageMenuItem
import com.saion.feature.mypage.impl.mypage.component.ProfileSection
import com.saion.feature.mypage.impl.mypage.component.versionMenuItem
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageEffect
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageSnackbarMessage
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageState
import com.saion.feature.mypage.impl.mypage.viewmodel.MyPageViewModel
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun MyPageScreen(
    modifier: Modifier = Modifier,
    onNotificationSettingsClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    onUpdateClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onWithdrawClick: () -> Unit = {},
    viewModel: MyPageViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val versionName = remember(context) { context.findVersionName() }

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is MyPageEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
        }
    }

    MyPageScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        versionName = versionName,
        onNotificationSettingsClick = onNotificationSettingsClick,
        onTermsClick = onTermsClick,
        onFeedbackClick = onFeedbackClick,
        onUpdateClick = onUpdateClick,
        onLogoutClick = onLogoutClick,
        onWithdrawClick = onWithdrawClick,
        modifier = modifier,
    )
}

@Composable
private fun MyPageScreen(
    uiState: MyPageState,
    snackbarHostState: SnackbarHostState,
    versionName: String,
    onNotificationSettingsClick: () -> Unit,
    onTermsClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SaionScaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        systemBarInset = SystemBarInset.None,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        containerColor = SaionTheme.colors.background.subtle,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .background(SaionTheme.colors.background.subtle),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            ProfileSection(uiState = uiState)

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                MyPageMenuCard(
                    items = persistentListOf(
                        MyPageMenuItem(
                            title = stringResource(R.string.mypage_notification_settings),
                            onClick = onNotificationSettingsClick,
                        ),
                        MyPageMenuItem(
                            title = stringResource(R.string.mypage_terms),
                            onClick = onTermsClick,
                        ),
                        MyPageMenuItem(
                            title = stringResource(R.string.mypage_feedback),
                            onClick = onFeedbackClick,
                        ),
                    ),
                )
                MyPageMenuCard(
                    items = persistentListOf(
                        MyPageMenuItem(
                            title = stringResource(R.string.mypage_logout),
                            onClick = onLogoutClick,
                        ),
                        MyPageMenuItem(
                            title = stringResource(R.string.mypage_withdraw),
                            onClick = onWithdrawClick,
                        ),
                    ),
                )
                MyPageMenuCard(
                    items = persistentListOf(
                        versionMenuItem(
                            versionName = versionName,
                            isLatestVersion = uiState.isLatestVersion,
                            onUpdateClick = onUpdateClick,
                        ),
                    ),
                )
            }
        }
    }
}

private fun MyPageSnackbarMessage.resolve(context: Context): String = when (this) {
    is MyPageSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is MyPageSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

private fun Context.findVersionName(): String = runCatching {
    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0)
    }
    packageInfo.versionName.orEmpty()
}.getOrDefault("알 수 없음")

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    SaionTheme {
        MyPageScreen(
            uiState = MyPageState(
                nickname = "장훈",
                profileImageUrl = null,
                avatarColorHex = "#6C757F",
                isLoading = false,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            versionName = "1.0.0",
            onNotificationSettingsClick = {},
            onTermsClick = {},
            onFeedbackClick = {},
            onUpdateClick = {},
            onLogoutClick = {},
            onWithdrawClick = {},
        )
    }
}
