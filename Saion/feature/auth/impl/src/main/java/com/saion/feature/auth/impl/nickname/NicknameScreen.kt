package com.saion.feature.auth.impl.nickname

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.model.member.NicknameValidation
import com.saion.core.model.member.NicknameValidationResult
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.theme.SaionTheme
import com.saion.feature.auth.impl.R
import com.saion.feature.auth.impl.nickname.component.NicknameBottomAction
import com.saion.feature.auth.impl.nickname.component.NicknameContent
import com.saion.feature.auth.impl.ui.resolve
import com.saion.feature.auth.impl.nickname.viewmodel.NicknameEffect
import com.saion.feature.auth.impl.nickname.viewmodel.NicknameIntent
import com.saion.feature.auth.impl.nickname.viewmodel.NicknameUiState
import com.saion.feature.auth.impl.nickname.viewmodel.NicknameViewModel

@Composable
internal fun NicknameScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit,
    viewModel: NicknameViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            NicknameEffect.NavigateBack -> onBack()
            NicknameEffect.NavigateComplete -> onComplete()
            is NicknameEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
        }
    }

    NicknameScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = { viewModel.dispatch(NicknameIntent.BackClicked) },
        onNicknameChange = { viewModel.dispatch(NicknameIntent.NicknameChanged(it)) },
        onSubmit = { viewModel.dispatch(NicknameIntent.SubmitClicked) },
    )
}

@Composable
private fun NicknameScreen(
    uiState: NicknameUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }.apply { requestFocus() }
    val defaultPlaceholder = stringResource(R.string.nickname_placeholder)

    SaionScaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                variant = TopBarVariant.Standard(onBack = onBackClick),
                modifier = Modifier.statusBarsPadding(),
            )
        },
        bottomBar = {
            NicknameBottomAction(
                validationResult = uiState.validation?.result,
                isSubmitEnabled = uiState.isSubmitEnabled,
                onSubmit = {
                    focusManager.clearFocus()
                    onSubmit()
                },
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            NicknameContent(
                nickname = uiState.nickname,
                placeholder = uiState.nickNamePlaceholder.ifBlank { defaultPlaceholder },
                validationResult = uiState.validation?.result,
                imageUrl = uiState.socialProfileImageUrl,
                avatarColorHex = uiState.avatarColorHex,
                focusRequester = focusRequester,
                onNicknameChange = onNicknameChange,
            )

            if (uiState.isSubmitting) SaionSpinner()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NicknameScreenPreview() {
    SaionTheme {
        NicknameScreen(
            uiState = NicknameUiState(
                nickname = "사이온",
                validation = NicknameValidation(
                    originalNickname = "사이온",
                    trimmedNickname = "사이온",
                    result = NicknameValidationResult.Valid,
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onNicknameChange = {},
            onSubmit = {},
        )
    }
}
