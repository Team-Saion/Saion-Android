package com.saion.core.model.term

data class Term(
    val id: String,
    val termCode: String,
    val title: String,
    val contentUrl: String?,
    val version: Int,
    val required: Boolean,
    val effectiveAt: String,
)
