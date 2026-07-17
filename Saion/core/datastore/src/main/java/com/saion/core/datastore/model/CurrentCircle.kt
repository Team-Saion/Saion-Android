package com.saion.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CurrentCircle(
    val selectedCircleId: String,
)
