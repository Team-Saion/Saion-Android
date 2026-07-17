package com.saion.feature.terms.impl.viewmodel

import com.saion.core.ui.viewmodel.UIEffect
import com.saion.feature.terms.impl.ui.TermsSnackbarMessage

internal sealed interface TermsUIEffect : UIEffect {
    data object NavigateBack : TermsUIEffect

    data object NavigateComplete : TermsUIEffect

    data class NavigateDetail(
        val title: String,
        val url: String,
    ) : TermsUIEffect

    data class ShowSnackbar(val message: TermsSnackbarMessage) : TermsUIEffect
}
