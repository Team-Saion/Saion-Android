package com.saion.feature.terms.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SaionSnackbarHost
import com.saion.core.ui.component.SaionSnackbarVariant
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.component.showSaionSnackbar
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.IconButtonSize
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.button.SaionButtonArea
import com.saion.ds.component.button.SaionIconButton
import com.saion.ds.component.button.SaionTextButton
import com.saion.ds.component.button.TextButtonSize
import com.saion.ds.component.feedback.SaionBottomSheet
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.selection.CheckBoxSize
import com.saion.ds.component.selection.SaionCheckBox
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.feature.terms.api.key.TermsMode
import com.saion.feature.terms.impl.model.TermsUIModel
import com.saion.feature.terms.impl.model.TermsUIModels
import com.saion.feature.terms.impl.ui.resolve
import com.saion.feature.terms.impl.viewmodel.TermsUIEffect
import com.saion.feature.terms.impl.viewmodel.TermsUIIntent
import com.saion.feature.terms.impl.viewmodel.TermsUIState
import com.saion.feature.terms.impl.viewmodel.TermsViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    mode: TermsMode,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onOpenTerm: (title: String, url: String) -> Unit,
) {
    val viewModel: TermsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.isBottomSheetVisible) {
        if (uiState.isBottomSheetVisible) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            TermsUIEffect.NavigateBack -> {
                launch { sheetState.hide() }.invokeOnCompletion { onBack() }
            }

            TermsUIEffect.NavigateComplete -> {
                launch { sheetState.hide() }.invokeOnCompletion { onComplete() }
            }

            is TermsUIEffect.NavigateDetail -> onOpenTerm(effect.title, effect.url)

            is TermsUIEffect.ShowSnackbar -> snackbarHostState.showSaionSnackbar(
                message = effect.message.resolve(context),
                variant = effect.message.variant(),
            )
        }
    }

    TermsScreen(
        mode = mode,
        uiState = uiState,
        sheetState = sheetState,
        snackbarHostState = snackbarHostState,
        onBackClick = { viewModel.dispatch(TermsUIIntent.BackClicked) },
        onToggleTerm = { id -> viewModel.dispatch(TermsUIIntent.ToggleTerm(id)) },
        onOpenTerm = { id -> viewModel.dispatch(TermsUIIntent.OpenTerm(id)) },
        onSubmit = { viewModel.dispatch(TermsUIIntent.SubmitAgreements) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TermsScreen(
    mode: TermsMode,
    uiState: TermsUIState,
    sheetState: SheetState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onToggleTerm: (String) -> Unit,
    onOpenTerm: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SaionSnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
    ) {
        if (uiState.isLoading && uiState.terms.items.isEmpty()) {
            SaionSpinner()
        }

        if (!uiState.isLoading && uiState.isBottomSheetVisible) {
            SaionBottomSheet(
                state = sheetState,
                onDismissRequest = onBackClick,
            ) {
                TermsList(
                    terms = uiState.terms,
                    mode = mode,
                    onToggleTerm = onToggleTerm,
                    onOpenTerm = onOpenTerm,
                )
                TermsBottomAction(
                    mode = mode,
                    isSubmitEnabled = uiState.isSubmitEnabled,
                    isLoading = uiState.isLoading,
                    onBackClick = onBackClick,
                    onSubmit = onSubmit,
                )
            }
        }
    }
}

private fun com.saion.feature.terms.impl.ui.TermsSnackbarMessage.variant(): SaionSnackbarVariant? = when (this) {
    is com.saion.feature.terms.impl.ui.TermsSnackbarMessage.Error -> SaionSnackbarVariant.Negative
    is com.saion.feature.terms.impl.ui.TermsSnackbarMessage.Res,
    is com.saion.feature.terms.impl.ui.TermsSnackbarMessage.Text -> null
}

@Composable
private fun TermsList(
    terms: TermsUIModels,
    mode: TermsMode,
    onToggleTerm: (String) -> Unit,
    onOpenTerm: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(items = terms.items, key = { item -> item.id }) { term ->
            TermsListItem(
                mode = mode,
                term = term,
                onToggle = { onToggleTerm(term.id) },
                onOpen = { onOpenTerm(term.id) },
                modifier = Modifier.height(28.dp),
            )
        }
    }
}

@Composable
private fun TermsListItem(
    mode: TermsMode,
    term: TermsUIModel,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickableModifier = if (mode == TermsMode.AGREEMENT) Modifier.noRippleClickable(onClick = onToggle) else Modifier

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = clickableModifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (mode == TermsMode.AGREEMENT) {
                SaionCheckBox(
                    isChecked = term.isChecked,
                    size = CheckBoxSize.SMALL,
                    onCheckedChange = { onToggle() },
                )
            }
            Text(
                text = buildAnnotatedString {
                    val prefix = if (term.required) {
                        stringResource(R.string.terms_required_prefix)
                    } else {
                        stringResource(R.string.terms_optional_prefix)
                    }
                    val prefixStyle = SaionTheme.typography.label1
                    withStyle(
                        SpanStyle(
                            fontSize = prefixStyle.fontSize,
                            fontWeight = prefixStyle.fontWeight,
                            letterSpacing = prefixStyle.letterSpacing,
                        ),
                    ) { append(prefix) }
                    append(" ${term.title}")
                },
                style = SaionTheme.typography.label1Subtle,
                modifier = Modifier.padding(start = if (mode == TermsMode.AGREEMENT) 8.dp else 0.dp),
            )
        }

        if (term.contentUrl.isNotBlank()) {
            SaionIconButton(
                icon = SaionIcons.ChevronRight,
                size = IconButtonSize.SMALL,
                tint = SaionTheme.colors.primary.subtle,
                onClick = onOpen,
            )
        }
    }
}

@Composable
private fun TermsBottomAction(
    mode: TermsMode,
    isSubmitEnabled: Boolean,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSubmit: () -> Unit,
) {
    if (mode == TermsMode.AGREEMENT) {
        SaionBottomCTA(
            contentPadding = PaddingValues(),
            lower = {
                SaionTextButton(
                    text = stringResource(R.string.terms_close),
                    size = TextButtonSize.MEDIUM,
                    onClick = onBackClick,
                )
            },
        ) {
            SaionButtonArea(
                mainButton = { modifier ->
                    SaionButton(
                        text = stringResource(R.string.terms_agree_and_next),
                        onClick = onSubmit,
                        size = ButtonSize.LARGE,
                        enabled = isSubmitEnabled && !isLoading,
                        modifier = modifier,
                    )
                },
            )
        }
    } else {
        SaionBottomCTA(contentPadding = PaddingValues()) {
            SaionButtonArea(
                mainButton = { modifier ->
                    SaionButton(
                        text = stringResource(R.string.terms_close),
                        onClick = onBackClick,
                        size = ButtonSize.LARGE,
                        modifier = modifier,
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun TermsAgreementScreenPreview() {
    SaionTheme {
        TermsScreen(
            mode = TermsMode.AGREEMENT,
            uiState = TermsUIState(
                isBottomSheetVisible = true,
                terms = TermsUIModels(
                    items = listOf(
                        TermsUIModel(
                            id = "1",
                            isChecked = true,
                            required = true,
                            contentUrl = "https://example.com",
                            title = "서비스 이용약관",
                        ),
                        TermsUIModel(
                            id = "2",
                            isChecked = false,
                            required = false,
                            contentUrl = "https://example.com/privacy",
                            title = "개인정보 처리방침",
                        ),
                    ).toImmutableList(),
                ),
            ),
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onToggleTerm = {},
            onOpenTerm = {},
            onSubmit = {},
        )
    }
}
