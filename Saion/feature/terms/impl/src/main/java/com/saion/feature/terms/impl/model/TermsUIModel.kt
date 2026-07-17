package com.saion.feature.terms.impl.model

import androidx.compose.runtime.Immutable

@Immutable
internal data class TermsUIModel(
    val id: String,
    val isChecked: Boolean,
    val required: Boolean,
    val contentUrl: String,
    val title: String,
) {
    fun toggle(): TermsUIModel = copy(isChecked = isChecked.not())
}
