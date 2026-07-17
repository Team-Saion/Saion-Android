package com.saion.feature.terms.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.ui.viewmodel.UIState
import com.saion.feature.terms.impl.model.TermsUIModels

@Immutable
internal data class TermsUIState(
    val isLoading: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val terms: TermsUIModels = TermsUIModels(),
) : UIState {
    val isSubmitEnabled: Boolean
        get() = terms.hasAllRequiredChecked
}
