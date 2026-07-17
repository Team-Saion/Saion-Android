package com.saion.feature.terms.impl.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.term.AgreeTermsUseCase
import com.saion.core.domain.usecase.term.GetActiveTermsUseCase
import com.saion.core.model.result.AppError
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.terms.impl.R
import com.saion.feature.terms.impl.model.TermsUIModel
import com.saion.feature.terms.impl.model.TermsUIModels
import com.saion.feature.terms.impl.ui.TermsSnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class TermsViewModel @Inject constructor(
    private val getActiveTermsUseCase: GetActiveTermsUseCase,
    private val agreeTermsUseCase: AgreeTermsUseCase,
) : BaseViewModel<TermsUIState, TermsUIEffect, TermsUIIntent>(TermsUIState()) {
    init {
        dispatch(TermsUIIntent.LoadTerms)
    }

    override fun handleIntent(intent: TermsUIIntent) {
        when (intent) {
            TermsUIIntent.BackClicked -> {
                viewModelScope.launch {
                    update { copy(isBottomSheetVisible = false) }
                    emitEffect(TermsUIEffect.NavigateBack)
                }
            }
            TermsUIIntent.LoadTerms -> loadTerms()
            is TermsUIIntent.OpenTerm -> openTerm(intent.id)
            TermsUIIntent.SubmitAgreements -> submitAgreements()
            is TermsUIIntent.ToggleTerm -> toggleTerm(intent.id)
        }
    }

    private fun loadTerms() {
        launchSafely(
            onStart = { update { copy(isLoading = true) } },
            onSuccess = { terms ->
                val uiTerms = TermsUIModels(
                    items = terms.map { term ->
                        TermsUIModel(
                            id = term.id,
                            isChecked = false,
                            required = term.required,
                            contentUrl = term.contentUrl.orEmpty(),
                            title = term.title,
                        )
                    }.toImmutableList(),
                )
                update {
                    copy(
                        isLoading = false,
                        isBottomSheetVisible = true,
                        terms = uiTerms,
                    )
                }
            },
            onFailure = { error ->
                update { copy(isLoading = false) }
                emitEffect(TermsUIEffect.ShowSnackbar(error.toDisplayMessage(R.string.terms_error_load)))
            },
            onFinally = { update { copy(isLoading = false) } },
        ) {
            getActiveTermsUseCase()
        }
    }

    private fun toggleTerm(id: String) {
        update { copy(terms = terms.toggle(id)) }
    }

    private fun openTerm(id: String) {
        val term = currentState.terms.findById(id) ?: return
        val url = term.contentUrl.takeIf(String::isNotBlank) ?: return

        viewModelScope.launch {
            emitEffect(
                TermsUIEffect.NavigateDetail(
                    title = term.title,
                    url = url,
                ),
            )
        }
    }

    private fun submitAgreements() {
        if (!currentState.terms.hasAllRequiredChecked) return

        val agreedTermIds = currentState.terms.checkedTermIdsAsLongOrNull()
            ?: run {
                viewModelScope.launch {
                    emitEffect(TermsUIEffect.ShowSnackbar(TermsSnackbarMessage.Res(R.string.terms_error_invalid_data)))
                }
                return
            }

        launchSafely(
            onStart = { update { copy(isLoading = true) } },
            onSuccess = {
                update {
                    copy(
                        isLoading = false,
                        isBottomSheetVisible = false,
                    )
                }
                emitEffect(TermsUIEffect.NavigateComplete)
            },
            onFailure = { error ->
                update {
                    copy(
                        isLoading = false,
                        isBottomSheetVisible = true,
                    )
                }
                emitEffect(TermsUIEffect.ShowSnackbar(error.toDisplayMessage(R.string.terms_error_submit)))
            },
            onFinally = { update { copy(isLoading = false) } },
        ) {
            agreeTermsUseCase(termIds = agreedTermIds)
        }
    }
}

private fun AppError.toDisplayMessage(defaultMessageResId: Int): TermsSnackbarMessage = toSnackbarMessage(
    defaultMessageResId = defaultMessageResId,
    textMessage = { value, resId -> TermsSnackbarMessage.Text(value, resId) },
    errorMessage = { error, resId -> TermsSnackbarMessage.Error(error, resId) },
)
