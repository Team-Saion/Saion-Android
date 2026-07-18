package com.saion.feature.invitation.impl

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.invitation.InvitationIssuer
import com.saion.core.ui.component.SaionProfile
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.icon.SaionIcons
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

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            InvitationAcceptEffect.Close -> onClose()
            is InvitationAcceptEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
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
                modifier = Modifier.statusBarsPadding(),
                variant = TopBarVariant.Standard(
                    title = null,
                    onBack = onCloseClick,
                ),
            )
        },
        bottomBar = {
            if (uiState.isLoading.not()) {
                InvitationBottomAction(
                    uiState = uiState,
                    onCloseClick = onCloseClick,
                    onAcceptClick = onAcceptClick,
                )
            }
        },
    ) {
        when {
            uiState.isLoading -> SaionSpinner()

            uiState.isExpired -> {
                InvitationCenteredLayout {
                    InvitationExpiredContent()
                }
            }

            uiState.detail == null -> {
                InvitationCenteredLayout {
                    InvitationFallback()
                }
            }

            else -> {
                InvitationCenteredLayout {
                    InvitationSuccessContent(detail = uiState.detail)
                }
            }
        }
    }
}

@Composable
private fun InvitationCenteredLayout(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content,
        )
    }
}

@Composable
private fun InvitationBottomAction(
    uiState: InvitationAcceptState,
    onCloseClick: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    SaionBottomCTA {
        SaionButton(
            text = when {
                uiState.isExpired -> stringResource(R.string.invitation_go_home)
                uiState.detail != null -> stringResource(R.string.invitation_join_now)
                else -> stringResource(R.string.invitation_close)
            },
            onClick = if (uiState.detail != null && uiState.isExpired.not()) onAcceptClick else onCloseClick,
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            size = ButtonSize.XLARGE,
            enabled = uiState.isAccepting.not(),
        )
    }
}

@Composable
private fun InvitationSuccessContent(detail: InvitationDetail) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        SaionProfile(
            nickname = detail.inviter.nickname,
            textStyle = SaionTheme.typography.heading1,
            imageUrl = null,
            avatarColorHex = "#F56262",
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = stringResource(
                R.string.invitation_accept_message,
                detail.inviter.nickname,
                detail.circleName,
            ),
            style = SaionTheme.typography.heading1,
            color = SaionTheme.colors.label.strong,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun InvitationExpiredContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = SaionIcons.Warning,
            contentDescription = null,
            tint = SaionTheme.colors.line.strong,
            modifier = Modifier.size(64.dp),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.invitation_expired_title),
                style = SaionTheme.typography.heading1,
                color = SaionTheme.colors.label.default,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.invitation_expired_description),
                style = SaionTheme.typography.body1,
                color = SaionTheme.colors.label.subtle,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun InvitationFallback() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.invitation_load_failed),
            style = SaionTheme.typography.heading1,
            color = SaionTheme.colors.label.strong,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.invitation_load_failed_description),
            style = SaionTheme.typography.heading1Subtle,
            color = SaionTheme.colors.label.subtle,
            textAlign = TextAlign.Center,
        )
    }
}

private fun InvitationAcceptSnackbarMessage.resolve(context: Context): String = when (this) {
    is InvitationAcceptSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is InvitationAcceptSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

@Preview(showBackground = true)
@Composable
private fun InvitationAcceptScreenPreview() {
    SaionTheme {
        InvitationAcceptScreen(
            uiState = InvitationAcceptState(
                isLoading = false,
                detail = InvitationDetail(
                    invitationId = "invite-1",
                    circleName = "비니네",
                    inviter = InvitationIssuer(
                        nickname = "수빈",
                        avatarColor = "#FFFFFF",
                    ),
                    expiresAt = "2026-07-30T00:00:00",
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onCloseClick = {},
            onAcceptClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InvitationAcceptExpiredScreenPreview() {
    SaionTheme {
        InvitationAcceptScreen(
            uiState = InvitationAcceptState(
                isLoading = false,
                isExpired = true,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onCloseClick = {},
            onAcceptClick = {},
        )
    }
}
