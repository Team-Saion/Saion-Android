package com.saion.feature.circlecreate.impl.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.saion.core.ui.viewmodel.UIState

@Immutable
internal data class CircleCreateUiState(
    val name: String = "",
    @param:StringRes val validationMessageResId: Int? = null,
    val isSubmitEnabled: Boolean = false,
    val isSubmitting: Boolean = false,
) : UIState
