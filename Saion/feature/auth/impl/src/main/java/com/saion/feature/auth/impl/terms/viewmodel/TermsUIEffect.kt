package com.saion.feature.auth.impl.terms.viewmodel

import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface TermsUIEffect : UIEffect {
    data object NavigateNext : TermsUIEffect

    data object NavigateBack : TermsUIEffect

    data object ShowTermsSheet : TermsUIEffect

    data object HideTermsSheet : TermsUIEffect

    data class OpenBrowser(val url: String) : TermsUIEffect

    data class ShowSnackbar(val message: String) : TermsUIEffect
}
