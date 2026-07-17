package com.saion.core.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface CurrentCircleLocalDataSource {
    fun observeSelectedCircleId(): Flow<String?>

    suspend fun getSelectedCircleId(): String?

    suspend fun saveSelectedCircleId(circleId: String?)

    suspend fun clearSelectedCircleId()
}
