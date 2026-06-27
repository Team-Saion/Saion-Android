package com.saion.core.network.model.term

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TermResponse(
    @SerialName("id")
    val id: String,
    @SerialName("termCode")
    val termCode: String,
    @SerialName("title")
    val title: String,
    @SerialName("contentUrl")
    val contentUrl: String? = null,
    @SerialName("version")
    val version: Int,
    @SerialName("required")
    val required: Boolean,
    @SerialName("effectiveAt")
    val effectiveAt: String,
)
