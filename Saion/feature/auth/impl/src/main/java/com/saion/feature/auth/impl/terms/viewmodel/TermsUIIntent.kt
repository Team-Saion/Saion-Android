package com.saion.feature.auth.impl.terms.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface TermsUIIntent : UIIntent {
    data object LoadTerms : TermsUIIntent

    data class ToggleTerm(val id: String) : TermsUIIntent

    data class OpenTerm(val id: String) : TermsUIIntent

    data object SubmitAgreements : TermsUIIntent

    data object BackClicked : TermsUIIntent
}
