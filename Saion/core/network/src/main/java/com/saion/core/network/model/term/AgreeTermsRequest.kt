package com.saion.core.network.model.term

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 약관 동의 요청입니다.
 */
@Serializable
data class AgreeTermsRequest(
    @SerialName("termIds")
    val termIds: List<Long>,
)
