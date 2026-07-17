package com.saion.feature.invitation.impl

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.error.getString
import com.saion.core.ui.error.resolveMessage
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.theme.SaionTheme
import com.saion.feature.invitation.impl.viewmodel.InvitationAcceptEffect
import com.saion.feature.invitation.impl.viewmodel.InvitationAcceptIntent
import com.saion.feature.invitation.impl.viewmodel.InvitationAcceptSnackbarMessage
import com.saion.feature.invitation.impl.viewmodel.InvitationAcceptState
import com.saion.feature.invitation.impl.viewmodel.InvitationAcceptViewModel

@Composable
internal fun InvitationAcceptScreen(
    token: String,
    onClose: () -> Unit,
    viewModel: InvitationAcceptViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(token) {
        viewModel.bind(token)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                InvitationAcceptEffect.Close -> onClose()
                is InvitationAcceptEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
            }
        }
    }

    InvitationAcceptScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCloseClick = { viewModel.dispatch(InvitationAcceptIntent.CloseClicked) },
        onAcceptClick = { viewModel.dispatch(InvitationAcceptIntent.AcceptClicked) },
    )
}

@Composable
private fun InvitationAcceptScreen(
    uiState: InvitationAcceptState,
    snackbarHostState: SnackbarHostState,
    onCloseClick: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        systemBarInset = SystemBarInset.None,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                variant = TopBarVariant.Standard(
                    title = stringResource(R.string.invitation_title),
                    onBack = onCloseClick,
                ),
            )
        },
    ) {
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    SaionSpinner()
                }
            }

            uiState.detail == null -> {
                InvitationFallback(
                    onCloseClick = onCloseClick,
                )
            }

            else -> {
                val detail = uiState.detail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(
                            R.string.invitation_inviter_format,
                            detail.inviter.nickname,
                            detail.circleName,
                        ),
                        style = SaionTheme.typography.title1,
                        color = SaionTheme.colors.label.strong,
                    )
                    Text(
                        text = stringResource(R.string.invitation_expire_format, detail.expiresAt),
                        style = SaionTheme.typography.body2,
                        color = SaionTheme.colors.label.subtle,
                    )
                    SaionButton(
                        text = stringResource(R.string.invitation_accept),
                        onClick = onAcceptClick,
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.LARGE,
                        enabled = uiState.isAccepting.not(),
                    )
                    SaionButton(
                        text = stringResource(R.string.invitation_close),
                        onClick = onCloseClick,
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.LARGE,
                        variant = ButtonVariant.NEUTRAL,
                        enabled = uiState.isAccepting.not(),
                    )
                }
            }
        }
    }
}

@Composable
private fun InvitationFallback(onCloseClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, androidx.compose.ui.Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(R.string.invitation_load_failed),
            style = SaionTheme.typography.title1,
            color = SaionTheme.colors.label.strong,
        )
        SaionButton(
            text = stringResource(R.string.invitation_close),
            onClick = onCloseClick,
            modifier = Modifier.fillMaxWidth(),
            size = ButtonSize.LARGE,
            variant = ButtonVariant.NEUTRAL,
        )
    }
}

private fun InvitationAcceptSnackbarMessage.resolve(context: Context): String = when (this) {
    is InvitationAcceptSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is InvitationAcceptSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}
