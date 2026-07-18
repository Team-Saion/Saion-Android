package com.saion.feature.profileedit.impl

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SaionSnackbarHost
import com.saion.core.ui.component.SaionSnackbarVariant
import com.saion.core.ui.component.showSaionSnackbar
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import androidx.compose.ui.tooling.preview.Preview
import com.saion.core.model.member.NicknameValidation
import com.saion.core.model.member.NicknameValidationResult
import com.saion.ds.theme.SaionTheme
import com.saion.feature.profileedit.impl.component.ProfileEditBottomAction
import com.saion.feature.profileedit.impl.component.ProfileEditContent

@Composable
fun AuthProfileEditScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    val viewModel: AuthProfileEditViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            ProfileEditEffect.NavigateBack -> onBack()
            ProfileEditEffect.NavigateComplete -> onComplete()
            is ProfileEditEffect.ShowSnackbar -> snackbarHostState.showSaionSnackbar(
                message = effect.message.resolve(context),
                variant = effect.message.variant(),
            )
        }
    }

    ProfileEditScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        uiConfig = ProfileEditUiConfig(
            titleResId = R.string.profile_edit_auth_title,
            placeholderResId = R.string.profile_edit_placeholder,
            submitResId = R.string.profile_edit_submit_auth,
        ),
        onProfileImageSelected = { viewModel.dispatch(ProfileEditIntent.ProfileImageSelected(it)) },
        onBackClick = { viewModel.dispatch(ProfileEditIntent.BackClicked) },
        onNicknameChange = { viewModel.dispatch(ProfileEditIntent.NicknameChanged(it)) },
        onSubmit = { viewModel.dispatch(ProfileEditIntent.SubmitClicked) },
    )
}

@Composable
fun MyPageProfileEditScreen(onBack: () -> Unit) {
    val viewModel: MyPageProfileEditViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            ProfileEditEffect.NavigateBack -> onBack()
            ProfileEditEffect.NavigateComplete -> onBack()
            is ProfileEditEffect.ShowSnackbar -> snackbarHostState.showSaionSnackbar(
                message = effect.message.resolve(context),
                variant = effect.message.variant(),
            )
        }
    }

    ProfileEditScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        uiConfig = ProfileEditUiConfig(
            titleResId = R.string.profile_edit_mypage_title,
            placeholderResId = R.string.profile_edit_placeholder,
            submitResId = R.string.profile_edit_submit_mypage,
        ),
        onProfileImageSelected = { viewModel.dispatch(ProfileEditIntent.ProfileImageSelected(it)) },
        onBackClick = { viewModel.dispatch(ProfileEditIntent.BackClicked) },
        onNicknameChange = { viewModel.dispatch(ProfileEditIntent.NicknameChanged(it)) },
        onSubmit = { viewModel.dispatch(ProfileEditIntent.SubmitClicked) },
    )
}

@Composable
private fun ProfileEditScreen(
    uiState: ProfileEditUiState,
    snackbarHostState: SnackbarHostState,
    uiConfig: ProfileEditUiConfig,
    onProfileImageSelected: (String?) -> Unit,
    onBackClick: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }.apply { requestFocus() }
    val defaultPlaceholder = stringResource(uiConfig.placeholderResId)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.toString()?.let(onProfileImageSelected)
    }

    SaionScaffold(
        snackbarHost = { SaionSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                variant = TopBarVariant.Standard(onBack = onBackClick),
                modifier = Modifier.statusBarsPadding(),
            )
        },
        bottomBar = {
            ProfileEditBottomAction(
                validationResult = uiState.validation?.result,
                isSubmitEnabled = uiState.isSubmitEnabled,
                submitText = stringResource(uiConfig.submitResId),
                onSubmit = {
                    focusManager.clearFocus()
                    onSubmit()
                },
                modifier = Modifier.navigationBarsPadding(),
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            ProfileEditContent(
                title = stringResource(uiConfig.titleResId),
                nickname = uiState.nickname,
                placeholder = uiState.nicknamePlaceholder.ifBlank { defaultPlaceholder },
                validationResult = uiState.validation?.result,
                imageUrl = uiState.selectedProfileImageUri ?: uiState.profileImageUrl,
                avatarColorHex = uiState.avatarColorHex,
                focusRequester = focusRequester,
                onProfileImageClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
                onNicknameChange = onNicknameChange,
            )

            if (uiState.isSubmitting) SaionSpinner()
        }
    }
}

private fun ProfileEditSnackbarMessage.variant(): SaionSnackbarVariant? = when (this) {
    is ProfileEditSnackbarMessage.Error -> SaionSnackbarVariant.Negative
    is ProfileEditSnackbarMessage.Text -> null
}

@Preview(showBackground = true)
@Composable
private fun ProfileEditScreenPreview() {
    SaionTheme {
        ProfileEditScreen(
            uiState = ProfileEditUiState(
                nickname = "사이온",
                validation = NicknameValidation(
                    originalNickname = "사이온",
                    trimmedNickname = "사이온",
                    result = NicknameValidationResult.Valid,
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            uiConfig = ProfileEditUiConfig(
                titleResId = R.string.profile_edit_auth_title,
                placeholderResId = R.string.profile_edit_placeholder,
                submitResId = R.string.profile_edit_submit_auth,
            ),
            onProfileImageSelected = {},
            onBackClick = {},
            onNicknameChange = {},
            onSubmit = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileEditScreen_MyPage_Preview() {
    SaionTheme {
        ProfileEditScreen(
            uiState = ProfileEditUiState(
                initialNickname = "사이온",
                nickname = "사이온2",
                validation = NicknameValidation(
                    originalNickname = "사이온2",
                    trimmedNickname = "사이온2",
                    result = NicknameValidationResult.Valid,
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            uiConfig = ProfileEditUiConfig(
                titleResId = R.string.profile_edit_mypage_title,
                placeholderResId = R.string.profile_edit_placeholder,
                submitResId = R.string.profile_edit_submit_mypage,
            ),
            onProfileImageSelected = {},
            onBackClick = {},
            onNicknameChange = {},
            onSubmit = {},
        )
    }
}
