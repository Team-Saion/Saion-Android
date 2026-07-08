package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationTypesResponse(
    @SerialName("value")
    val value: String,
    @SerialName("label")
    val label: String,
)
