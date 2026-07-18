package com.saion.core.datastore.datasource

import com.saion.core.datastore.model.CircleListCache
import kotlinx.coroutines.flow.Flow

interface CircleLocalDataSource {
    fun observeCircles(): Flow<CircleListCache>

    suspend fun getCircles(): CircleListCache

    suspend fun saveCircles(cache: CircleListCache)

    suspend fun clearCircles()
}
