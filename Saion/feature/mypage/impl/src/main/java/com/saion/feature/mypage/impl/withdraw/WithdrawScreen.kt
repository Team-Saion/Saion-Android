package com.saion.feature.mypage.impl.withdraw

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SaionSnackbarHost
import com.saion.core.ui.component.SaionSnackbarVariant
import com.saion.core.ui.component.showSaionSnackbar
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.button.SaionButtonArea
import com.saion.ds.component.feedback.SaionConfirmDialog
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.input.SaionTextArea
import com.saion.ds.component.input.SaionTextAreaVariant
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.theme.SaionTheme
import com.saion.feature.mypage.impl.R
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawEffect
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawIntent
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawSnackbarMessage
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawState
import com.saion.feature.mypage.impl.withdraw.viewmodel.WithdrawViewModel

@Composable
internal fun WithdrawScreen(
    onBack: () -> Unit,
    viewModel: WithdrawViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is WithdrawEffect.ShowSnackbar -> {
                snackbarHostState.showSaionSnackbar(
                    message = effect.message.resolve(context),
                    variant = effect.message.variant(),
                )
            }

            WithdrawEffect.WithdrawCompleted -> GlobalUiEventBus.emit(GlobalUiEvent.SessionExpired)
        }
    }

    WithdrawScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onReasonChanged = { viewModel.dispatch(WithdrawIntent.ReasonChanged(it)) },
        onWithdrawClick = { viewModel.dispatch(WithdrawIntent.ClickWithdraw) },
        onWithdrawDismiss = { viewModel.dispatch(WithdrawIntent.DismissWithdrawDialog) },
        onWithdrawConfirm = { viewModel.dispatch(WithdrawIntent.ConfirmWithdraw) },
    )
}

@Composable
private fun WithdrawScreen(
    uiState: WithdrawState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onReasonChanged: (String) -> Unit,
    onWithdrawClick: () -> Unit,
    onWithdrawDismiss: () -> Unit,
    onWithdrawConfirm: () -> Unit,
) {
    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SaionSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                modifier = Modifier.statusBarsPadding(),
                variant = TopBarVariant.Standard(
                    title = stringResource(R.string.withdraw_title),
                    onBack = onBack,
                ),
            )
        },
        bottomBar = {
            SaionBottomCTA(
                modifier = Modifier.navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            ) {
                SaionButtonArea(
                    mainButton = { buttonModifier ->
                        SaionButton(
                            text = stringResource(R.string.withdraw_submit),
                            onClick = onWithdrawClick,
                            modifier = buttonModifier,
                            size = ButtonSize.XLARGE,
                            variant = ButtonVariant.DANGER,
                            enabled = uiState.isSubmitting.not(),
                        )
                    },
                )
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            WithdrawContent(
                reason = uiState.reason,
                onReasonChanged = onReasonChanged,
            )

            if (uiState.isSubmitting) {
                SaionSpinner()
            }
        }
    }

    if (uiState.showWithdrawDialog) {
        SaionConfirmDialog(
            title = stringResource(R.string.withdraw_dialog_title),
            description = stringResource(R.string.withdraw_dialog_description),
            confirmButtonText = stringResource(R.string.withdraw_dialog_confirm),
            onConfirm = onWithdrawConfirm,
            dismissButtonText = stringResource(R.string.withdraw_dialog_cancel),
            onDismiss = onWithdrawDismiss,
            isDanger = true,
        )
    }
}

@Composable
private fun WithdrawContent(
    reason: String,
    onReasonChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.withdraw_heading),
                style = SaionTheme.typography.title1Strong,
                color = SaionTheme.colors.label.default,
            )
            Text(
                text = stringResource(R.string.withdraw_subheading),
                style = SaionTheme.typography.body3,
                color = SaionTheme.colors.label.subtle,
            )
        }

        SaionTextArea(
            value = reason,
            onValueChange = onReasonChanged,
            modifier = Modifier.fillMaxWidth(),
            variant = SaionTextAreaVariant.BOX,
            placeholder = stringResource(R.string.withdraw_reason_placeholder),
            minLines = 4,
            maxLines = 4,
        )
    }
}

private fun WithdrawSnackbarMessage.resolve(context: Context): String = when (this) {
    is WithdrawSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is WithdrawSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

private fun WithdrawSnackbarMessage.variant(): SaionSnackbarVariant? = when (this) {
    is WithdrawSnackbarMessage.Error -> SaionSnackbarVariant.Negative
    is WithdrawSnackbarMessage.Text -> null
}

@Preview(showBackground = true)
@Composable
private fun WithdrawScreenPreview() {
    SaionTheme {
        WithdrawScreen(
            uiState = WithdrawState(reason = "일정 관리가 저와 맞지 않았어요."),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onReasonChanged = {},
            onWithdrawClick = {},
            onWithdrawDismiss = {},
            onWithdrawConfirm = {},
        )
    }
}
