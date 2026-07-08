package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.CircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.CircleRemoteDataSource
import com.saion.core.network.model.circle.CircleSummaryResponse
import javax.inject.Inject

internal class CircleRepositoryImpl @Inject constructor(
    private val circleRemoteDataSource: CircleRemoteDataSource,
) : CircleRepository {
    override suspend fun listCircles(): AppResult<List<CircleSummary>> = safeRequest(
        request = { circleRemoteDataSource.listCircles() },
    ) { response ->
        AppResult.Success(response.map(CircleSummaryResponse::toDomain))
    }

    override suspend fun createCircle(name: String): AppResult<CircleSummary> = safeRequest(
        request = { circleRemoteDataSource.createCircle(name = name) },
    ) { response ->
        AppResult.Success(response.toDomain())
    }

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary> = safeRequest(
        request = {
            circleRemoteDataSource.transferInitiator(
                circleId = circleId,
                targetMemberId = targetMemberId,
            )
        },
    ) { response ->
        AppResult.Success(response.toDomain())
    }
}

private fun CircleSummaryResponse.toDomain(): CircleSummary = CircleSummary(
    circleId = circleId,
    name = name,
    ownerId = ownerId,
)
