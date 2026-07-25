package com.saion.feature.circlecreate.impl

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SaionSnackbarHost
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.feature.circlecreate.impl.component.CircleCreateBottomAction
import com.saion.feature.circlecreate.impl.component.CircleCreateContent
import com.saion.feature.circlecreate.impl.viewmodel.CircleJoinEffect
import com.saion.feature.circlecreate.impl.viewmodel.CircleJoinIntent
import com.saion.feature.circlecreate.impl.viewmodel.CircleJoinUiState
import com.saion.feature.circlecreate.impl.viewmodel.CircleJoinViewModel

@Composable
internal fun CircleJoinScreen(
    onClose: () -> Unit,
    onOpenInvitation: (String) -> Unit,
    viewModel: CircleJoinViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is CircleJoinEffect.OpenInvitation -> onOpenInvitation(effect.token)
        }
    }

    CircleJoinScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCloseClick = onClose,
        onCodeChange = { viewModel.dispatch(CircleJoinIntent.CodeChanged(it)) },
        onSubmit = { viewModel.dispatch(CircleJoinIntent.SubmitClicked) },
    )
}

@Composable
private fun CircleJoinScreen(
    uiState: CircleJoinUiState,
    snackbarHostState: SnackbarHostState,
    onCloseClick: () -> Unit,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val validationMessage = uiState.validationMessageResId?.let { stringResource(it) }

    SaionScaffold(
        snackbarHost = { SaionSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                variant = TopBarVariant.Standard(
                    title = stringResource(R.string.circle_join_title),
                    onBack = onCloseClick,
                    navigationIcon = SaionIcons.ChevronLeft,
                ),
                modifier = Modifier.statusBarsPadding(),
            )
        },
        bottomBar = {
            CircleCreateBottomAction(
                supportingMessage = validationMessage,
                supportingMessageColor = validationMessage?.let { SaionTheme.colors.status.negative.default },
                submitText = stringResource(R.string.circle_join_submit),
                isSubmitEnabled = uiState.isSubmitEnabled,
                isSubmitting = false,
                onSubmit = onSubmit,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
        systemBarInset = SystemBarInset.None,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            CircleCreateContent(
                heading = stringResource(R.string.circle_join_heading),
                name = uiState.code,
                placeholder = stringResource(R.string.circle_join_placeholder),
                onNameChange = onCodeChange,
                modifier = Modifier.offset(y = maxHeight * 0.2f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CircleJoinScreenPreview() {
    SaionTheme {
        CircleJoinScreen(
            uiState = CircleJoinUiState(code = "invite-token", isSubmitEnabled = true),
            snackbarHostState = remember { SnackbarHostState() },
            onCloseClick = {},
            onCodeChange = {},
            onSubmit = {},
        )
    }
}
