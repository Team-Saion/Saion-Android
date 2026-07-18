package com.saion.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class CircleListCache(
    val circles: List<CircleSummaryCache> = emptyList(),
)

@Serializable
data class CircleSummaryCache(
    val circleId: String,
    val name: String,
    val ownerId: String,
)
