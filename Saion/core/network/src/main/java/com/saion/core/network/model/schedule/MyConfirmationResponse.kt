package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyConfirmationResponse(
    @SerialName("confirmationId")
    val confirmationId: Long,
    @SerialName("confirmationType")
    val confirmationType: String,
)
