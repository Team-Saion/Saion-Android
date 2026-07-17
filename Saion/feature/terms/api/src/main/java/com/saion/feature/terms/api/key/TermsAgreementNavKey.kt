package com.saion.feature.terms.api.key

import com.saion.feature.main.api.key.MainTabNavKey
import kotlinx.serialization.Serializable

@Serializable
data class TermsAgreementNavKey(
    val mode: TermsMode = TermsMode.AGREEMENT,
) : MainTabNavKey
