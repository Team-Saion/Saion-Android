package com.saion.feature.terms.impl.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal data class TermsUIModels(val items: ImmutableList<TermsUIModel> = persistentListOf()) {
    val hasAllRequiredChecked: Boolean
        get() = items.all { term -> !term.required || term.isChecked }

    fun toggle(id: String): TermsUIModels = copy(
        items = items.map { term ->
            if (term.id == id) term.toggle() else term
        }.toImmutableList(),
    )

    fun findById(id: String): TermsUIModel? = items.firstOrNull { it.id == id }

    fun checkedTermIdsAsLongOrNull(): List<Long>? = buildList {
        items.filter(TermsUIModel::isChecked).forEach { term ->
            add(term.id.toLongOrNull() ?: return null)
        }
    }
}
