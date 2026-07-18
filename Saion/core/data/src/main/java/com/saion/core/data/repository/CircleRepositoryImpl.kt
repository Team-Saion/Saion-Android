package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.datastore.datasource.CircleLocalDataSource
import com.saion.core.datastore.model.CircleListCache
import com.saion.core.datastore.model.CircleSummaryCache
import com.saion.core.domain.repository.CircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.CircleRemoteDataSource
import com.saion.core.network.model.circle.CircleSummaryResponse
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class CircleRepositoryImpl @Inject constructor(
    private val circleLocalDataSource: CircleLocalDataSource,
    private val circleRemoteDataSource: CircleRemoteDataSource,
) : CircleRepository {
    override fun observeCircles(): Flow<List<CircleSummary>> = circleLocalDataSource.observeCircles().map { cache ->
        cache.circles.map(CircleSummaryCache::toDomain)
    }

    override suspend fun listCircles(): AppResult<List<CircleSummary>> {
        val cachedCircles = circleLocalDataSource.getCircles().circles.map(CircleSummaryCache::toDomain)
        if (cachedCircles.isNotEmpty()) {
            return AppResult.Success(cachedCircles)
        }
        return refreshCircles()
    }

    override suspend fun refreshCircles(): AppResult<List<CircleSummary>> = safeRequest(
        request = { circleRemoteDataSource.listCircles() },
    ) { response ->
        val circles = response.map(CircleSummaryResponse::toDomain)
        circleLocalDataSource.saveCircles(CircleListCache(circles = circles.map(CircleSummary::toCache)))
        AppResult.Success(circles)
    }

    override suspend fun createCircle(name: String): AppResult<CircleSummary> = safeRequest(
        request = { circleRemoteDataSource.createCircle(name = name) },
    ) { response ->
        val createdCircle = response.toDomain()
        val updatedCache = circleLocalDataSource.getCircles().circles
            .filterNot { it.circleId == createdCircle.circleId } + createdCircle.toCache()
        circleLocalDataSource.saveCircles(CircleListCache(circles = updatedCache))
        AppResult.Success(createdCircle)
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
        val updatedCircle = response.toDomain()
        val updatedCache = circleLocalDataSource.getCircles().circles
            .filterNot { it.circleId == updatedCircle.circleId } + updatedCircle.toCache()
        circleLocalDataSource.saveCircles(CircleListCache(circles = updatedCache))
        AppResult.Success(updatedCircle)
    }

    override suspend fun leave(circleId: String): AppResult<Unit> = safeRequest(
        request = { circleRemoteDataSource.leave(circleId = circleId) },
    ) {
        val updatedCache = circleLocalDataSource.getCircles().circles.filterNot { it.circleId == circleId }
        circleLocalDataSource.saveCircles(CircleListCache(circles = updatedCache))
        AppResult.Success(Unit)
    }
}

private fun CircleSummaryResponse.toDomain(): CircleSummary = CircleSummary(
    circleId = circleId,
    name = name,
    ownerId = ownerId,
)

private fun CircleSummaryCache.toDomain(): CircleSummary = CircleSummary(
    circleId = circleId,
    name = name,
    ownerId = ownerId,
)

private fun CircleSummary.toCache(): CircleSummaryCache = CircleSummaryCache(
    circleId = circleId,
    name = name,
    ownerId = ownerId,
)
