package com.saion.feature.auth.impl.terms.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.term.AgreeTermsUseCase
import com.saion.core.domain.usecase.term.GetActiveTermsUseCase
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.auth.impl.terms.model.TermsUIModel
import com.saion.feature.auth.impl.terms.model.TermsUIModels
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
                        terms = uiTerms,
                    )
                }
                emitEffect(TermsUIEffect.ShowTermsSheet)
            },
            onFailure = { error ->
                update { copy(isLoading = false) }
                emitEffect(TermsUIEffect.ShowSnackbar(error.toDisplayMessage(defaultMessage = "약관 정보를 불러오지 못했습니다.")))
            },
            onFinally = { update { copy(isLoading = false) } },
        ) {
            getActiveTermsUseCase()
        }
    }

    private fun toggleTerm(id: String) {
        update {
            copy(
                terms = terms.toggle(id),
            )
        }
    }

    private fun openTerm(id: String) {
        val url = currentState.terms.findContentUrl(id) ?: return

        viewModelScope.launch {
            emitEffect(TermsUIEffect.OpenBrowser(url))
        }
    }

    private fun submitAgreements() {
        if (!currentState.terms.hasAllRequiredChecked) {
            return
        }

        val agreedTermIds = currentState.terms.checkedTermIdsAsLongOrNull()
            ?: run {
                viewModelScope.launch {
                    emitEffect(TermsUIEffect.ShowSnackbar("약관 정보를 확인한 뒤 다시 시도해주세요."))
                }
                return
            }

        update { copy(isLoading = true) }
        viewModelScope.launch {
            emitEffect(TermsUIEffect.HideTermsSheet)
        }

        launchSafely(
            onSuccess = {
                update { copy(isLoading = false) }
                emitEffect(TermsUIEffect.NavigateNext)
            },
            onFailure = { error ->
                update { copy(isLoading = false) }
                emitEffect(TermsUIEffect.ShowTermsSheet)
                emitEffect(TermsUIEffect.ShowSnackbar(error.toDisplayMessage(defaultMessage = "약관 동의에 실패했습니다. 다시 시도해주세요.")))
            },
            onFinally = { update { copy(isLoading = false) } },
        ) {
            agreeTermsUseCase(termIds = agreedTermIds)
        }
    }
}

private fun AppError.toDisplayMessage(defaultMessage: String): String = when (this) {
    is AppError.Business -> message ?: defaultMessage
    is AppError.Unknown -> message ?: defaultMessage
    is AppError.NetworkUnavailable -> "네트워크 연결을 확인한 뒤 다시 시도해주세요."
    is AppError.Timeout -> "응답이 지연되고 있습니다. 다시 시도해주세요."
    is AppError.ServerUnavailable -> "서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요."
    is AppError.Unauthorized -> "로그인 정보가 만료되었습니다. 다시 로그인해주세요."
}
