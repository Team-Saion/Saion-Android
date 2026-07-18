package com.saion.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.saion.core.datastore.model.CircleListCache
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

internal class DefaultCircleLocalDataSource @Inject constructor(
    private val dataStore: DataStore<CircleListCache>,
) : CircleLocalDataSource {
    override fun observeCircles(): Flow<CircleListCache> = dataStore.data

    override suspend fun getCircles(): CircleListCache = dataStore.data.firstOrNull() ?: CircleListCache()

    override suspend fun saveCircles(cache: CircleListCache) {
        dataStore.updateData { cache }
    }

    override suspend fun clearCircles() {
        dataStore.updateData { CircleListCache() }
    }
}
