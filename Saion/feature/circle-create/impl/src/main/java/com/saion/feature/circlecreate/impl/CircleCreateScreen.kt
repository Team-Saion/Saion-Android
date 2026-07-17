package com.saion.feature.circlecreate.impl

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.feature.circlecreate.impl.component.CircleCreateBottomAction
import com.saion.feature.circlecreate.impl.component.CircleCreateContent
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateEffect
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateIntent
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateSnackbarMessage
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateUiState
import com.saion.feature.circlecreate.impl.viewmodel.CircleCreateViewModel

@Composable
internal fun CircleCreateScreen(
    onClose: () -> Unit,
    viewModel: CircleCreateViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            CircleCreateEffect.Close -> onClose()
            is CircleCreateEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
        }
    }

    CircleCreateScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCloseClick = onClose,
        onNameChange = { viewModel.dispatch(CircleCreateIntent.NameChanged(it)) },
        onSubmit = { viewModel.dispatch(CircleCreateIntent.SubmitClicked) },
    )
}

@Composable
private fun CircleCreateScreen(
    uiState: CircleCreateUiState,
    snackbarHostState: SnackbarHostState,
    onCloseClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val validationMessage = uiState.validationMessageResId?.let { stringResource(it) }

    SaionScaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                variant = TopBarVariant.Standard(
                    title = stringResource(R.string.circle_create_title),
                    onBack = onCloseClick,
                    navigationIcon = SaionIcons.ChevronLeft,
                ),
                modifier = Modifier.statusBarsPadding(),
            )
        },
        bottomBar = {
            CircleCreateBottomAction(
                nameLength = uiState.name.length,
                isNameTooLong = uiState.name.trim().length > 20,
                validationMessage = validationMessage,
                isSubmitEnabled = uiState.isSubmitEnabled,
                isSubmitting = uiState.isSubmitting,
                onSubmit = onSubmit,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
        systemBarInset = SystemBarInset.None,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            CircleCreateContent(
                name = uiState.name,
                placeholder = stringResource(R.string.circle_create_placeholder),
                onNameChange = onNameChange,
            )

            if (uiState.isSubmitting) SaionSpinner()
        }
    }
}

private fun CircleCreateSnackbarMessage.resolve(context: Context): String = when (this) {
    is CircleCreateSnackbarMessage.Res -> context.getString(resId)
    is CircleCreateSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is CircleCreateSnackbarMessage.Error -> error.resolveMessage(
        context = context,
        defaultMessageResId = defaultMessageResId,
        fallbackToDefaultForSystemErrors = true,
    )
}

@Preview(showBackground = true)
@Composable
private fun CircleCreateScreenPreview() {
    SaionTheme {
        CircleCreateScreen(
            uiState = CircleCreateUiState(name = "비니네", isSubmitEnabled = true),
            snackbarHostState = remember { SnackbarHostState() },
            onCloseClick = {},
            onNameChange = {},
            onSubmit = {},
        )
    }
}
