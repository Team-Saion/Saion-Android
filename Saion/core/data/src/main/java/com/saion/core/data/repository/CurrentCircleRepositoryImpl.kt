package com.saion.core.data.repository

import com.saion.core.datastore.datasource.CurrentCircleLocalDataSource
import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class CurrentCircleRepositoryImpl @Inject constructor(
    private val currentCircleLocalDataSource: CurrentCircleLocalDataSource,
    private val circleRepository: CircleRepository,
) : CurrentCircleRepository {
    override fun observeCurrentCircleId(): Flow<String?> = currentCircleLocalDataSource.observeSelectedCircleId()

    override suspend fun getCurrentCircleId(): String? = currentCircleLocalDataSource.getSelectedCircleId()

    override suspend fun selectCircle(circleId: String): AppResult<Unit> = when (val result = circleRepository.listCircles()) {
        is AppResult.Success -> {
            if (result.data.any { it.circleId == circleId }) {
                currentCircleLocalDataSource.saveSelectedCircleId(circleId)
                AppResult.Success(Unit)
            } else {
                AppResult.Failure(AppError.Unknown(message = "Selected circle is missing or invalid."))
            }
        }

        is AppResult.Failure -> result
    }

    override suspend fun syncCurrentCircle(): AppResult<String?> = when (val result = circleRepository.listCircles()) {
        is AppResult.Success -> {
            val resolvedCircleId = resolveCircleId(
                circles = result.data,
                selectedCircleId = currentCircleLocalDataSource.getSelectedCircleId(),
            )
            currentCircleLocalDataSource.saveSelectedCircleId(resolvedCircleId)
            AppResult.Success(resolvedCircleId)
        }

        is AppResult.Failure -> result
    }

    override suspend fun clearCurrentCircle() {
        currentCircleLocalDataSource.clearSelectedCircleId()
    }
}

private fun resolveCircleId(
    circles: List<CircleSummary>,
    selectedCircleId: String?,
): String? {
    if (circles.isEmpty()) return null
    if (selectedCircleId != null && circles.any { it.circleId == selectedCircleId }) {
        return selectedCircleId
    }
    return circles.first().circleId
}
