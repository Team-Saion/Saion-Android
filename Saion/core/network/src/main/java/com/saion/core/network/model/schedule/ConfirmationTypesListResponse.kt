package com.saion.core.network.model.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationTypesListResponse(
    @SerialName("confirmationTypes")
    val confirmationTypes: List<ConfirmationTypesResponse>,
)
