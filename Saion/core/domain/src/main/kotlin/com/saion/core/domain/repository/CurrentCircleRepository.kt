package com.saion.core.domain.repository

import com.saion.core.model.result.AppResult
import kotlinx.coroutines.flow.Flow

interface CurrentCircleRepository {
    fun observeCurrentCircleId(): Flow<String?>

    suspend fun getCurrentCircleId(): String?

    suspend fun selectCircle(circleId: String): AppResult<Unit>

    suspend fun syncCurrentCircle(): AppResult<String?>

    suspend fun clearCurrentCircle()
}
