package com.saion.core.domain.repository

import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.flow.Flow

interface CircleRepository {
    fun observeCircles(): Flow<List<CircleSummary>>

    suspend fun listCircles(): AppResult<List<CircleSummary>>

    suspend fun refreshCircles(): AppResult<List<CircleSummary>>

    suspend fun createCircle(name: String): AppResult<CircleSummary>

    suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary>

    suspend fun leave(circleId: String): AppResult<Unit>
}
