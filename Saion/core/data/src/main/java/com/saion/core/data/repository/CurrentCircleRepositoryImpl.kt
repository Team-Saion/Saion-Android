package com.saion.core.data.repository

import com.saion.core.datastore.datasource.CurrentCircleLocalDataSource
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class CurrentCircleRepositoryImpl @Inject constructor(
    private val currentCircleLocalDataSource: CurrentCircleLocalDataSource,
) : CurrentCircleRepository {
    override fun observeCurrentCircleId(): Flow<String?> = currentCircleLocalDataSource.observeSelectedCircleId()

    override suspend fun getCurrentCircleId(): String? = currentCircleLocalDataSource.getSelectedCircleId()

    override suspend fun selectCircle(circleId: String): AppResult<Unit> {
        currentCircleLocalDataSource.saveSelectedCircleId(circleId)
        return AppResult.Success(Unit)
    }

    override suspend fun clearCurrentCircle() {
        currentCircleLocalDataSource.clearSelectedCircleId()
    }
}
