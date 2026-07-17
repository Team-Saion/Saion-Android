package com.saion.feature.terms.api.key

import kotlinx.serialization.Serializable

@Serializable
enum class TermsMode {
    AGREEMENT,
    READ_ONLY,
}
