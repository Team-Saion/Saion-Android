package com.saion.feature.auth.impl.terms.viewmodel

import com.saion.core.ui.viewmodel.UIEffect
import com.saion.feature.auth.impl.ui.AuthSnackbarMessage

internal sealed interface TermsUIEffect : UIEffect {
    data object NavigateNext : TermsUIEffect

    data object NavigateBack : TermsUIEffect

    data class OpenBrowser(val url: String) : TermsUIEffect

    data class ShowSnackbar(val message: AuthSnackbarMessage) : TermsUIEffect
}
