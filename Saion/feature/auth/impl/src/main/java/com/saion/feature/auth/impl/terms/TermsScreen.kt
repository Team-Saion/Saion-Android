package com.saion.feature.auth.impl.terms

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.ui.component.SaionScaffold
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
import com.saion.feature.auth.impl.R
import com.saion.feature.auth.impl.terms.model.TermsUIModel
import com.saion.feature.auth.impl.terms.model.TermsUIModels
import com.saion.feature.auth.impl.ui.resolve
import com.saion.feature.auth.impl.terms.viewmodel.TermsUIEffect
import com.saion.feature.auth.impl.terms.viewmodel.TermsUIIntent
import com.saion.feature.auth.impl.terms.viewmodel.TermsUIState
import com.saion.feature.auth.impl.terms.viewmodel.TermsViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TermsScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: TermsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.isBottomSheetVisible) {
        if (uiState.isBottomSheetVisible) sheetState.show() else sheetState.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                TermsUIEffect.NavigateBack -> {
                    launch { sheetState.hide() }.invokeOnCompletion { onBack() }
                }

                TermsUIEffect.NavigateNext -> {
                    launch { sheetState.hide() }.invokeOnCompletion { onContinue() }
                }

                is TermsUIEffect.OpenBrowser -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, effect.url.toUri()))
                }

                is TermsUIEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
            }
        }
    }

    TermsScreen(
        uiState = uiState,
        sheetState = sheetState,
        snackbarHostState = snackbarHostState,
        onBackClick = { viewModel.dispatch(intent = TermsUIIntent.BackClicked) },
        onToggleTerm = { id -> viewModel.dispatch(intent = TermsUIIntent.ToggleTerm(id)) },
        onOpenTerm = { id -> viewModel.dispatch(intent = TermsUIIntent.OpenTerm(id)) },
        onSubmit = { viewModel.dispatch(intent = TermsUIIntent.SubmitAgreements) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TermsScreen(
    uiState: TermsUIState,
    sheetState: SheetState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onToggleTerm: (String) -> Unit,
    onOpenTerm: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    SaionScaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
    ) {
        if (uiState.isLoading && uiState.terms.items.isEmpty()) {
            SaionSpinner()
        }

        if (!uiState.isLoading && uiState.isBottomSheetVisible) {
            SaionBottomSheet(
                state = sheetState,
                onDismissRequest = onBackClick,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp),
            ) {
                TermsList(
                    terms = uiState.terms,
                    onToggleTerm = onToggleTerm,
                    onOpenTerm = onOpenTerm,
                )
                TermsBottomAction(
                    isSubmitEnabled = uiState.isSubmitEnabled,
                    isLoading = uiState.isLoading,
                    onBackClick = onBackClick,
                    onSubmit = onSubmit,
                )
            }
        }
    }
}

@Composable
private fun TermsList(
    terms: TermsUIModels,
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
    term: TermsUIModel,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.noRippleClickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SaionCheckBox(
                isChecked = term.isChecked,
                size = CheckBoxSize.SMALL,
                onCheckedChange = { onToggle() },
            )

            Text(
                text = buildAnnotatedString {
                    val prefixStyle = SaionTheme.typography.label1
                    val prefix = stringResource(
                        if (term.required) {
                            R.string.terms_required_prefix
                        } else {
                            R.string.terms_optional_prefix
                        },
                    )
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
                modifier = Modifier.padding(start = 8.dp),
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
    isSubmitEnabled: Boolean,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSubmit: () -> Unit,
) {
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
                    modifier = modifier,
                    size = ButtonSize.LARGE,
                    enabled = isSubmitEnabled && !isLoading,
                    onClick = onSubmit,
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun TermsScreenPreview() {
    val sheetState = rememberModalBottomSheetState()

    SaionTheme {
        TermsScreen(
            uiState = TermsUIState(
                terms = TermsUIModels(
                    items = List(4) {
                        TermsUIModel(
                            id = "$it",
                            isChecked = true,
                            required = true,
                            contentUrl = "",
                            title = "Term $it",
                        )
                    }.toImmutableList(),
                ),
            ),
            sheetState = sheetState,
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onToggleTerm = {},
            onOpenTerm = {},
            onSubmit = {},
        )
    }
}
