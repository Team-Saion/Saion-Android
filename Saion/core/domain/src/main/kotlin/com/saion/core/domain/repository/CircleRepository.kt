package com.saion.core.domain.repository

import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult

interface CircleRepository {
    suspend fun listCircles(): AppResult<List<CircleSummary>>

    suspend fun createCircle(name: String): AppResult<CircleSummary>

    suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary>
}
