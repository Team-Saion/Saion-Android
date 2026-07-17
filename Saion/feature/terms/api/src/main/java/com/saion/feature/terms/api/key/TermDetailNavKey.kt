package com.saion.feature.terms.api.key

import com.saion.feature.main.api.key.MainTabNavKey
import kotlinx.serialization.Serializable

@Serializable
data class TermDetailNavKey(
    val url: String,
) : MainTabNavKey
