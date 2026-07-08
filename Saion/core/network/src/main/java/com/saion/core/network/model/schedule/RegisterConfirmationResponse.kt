package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterConfirmationResponse(
    @SerialName("confirmationType")
    val confirmationType: String,
)
