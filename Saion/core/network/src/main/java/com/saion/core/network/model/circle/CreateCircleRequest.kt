package com.saion.core.network.model.circle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCircleRequest(
    @SerialName("name")
    val name: String,
)
