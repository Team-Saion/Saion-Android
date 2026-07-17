package com.saion.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.saion.core.datastore.model.CurrentCircle
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

internal class DefaultCurrentCircleLocalDataSource @Inject constructor(
    private val dataStore: DataStore<CurrentCircle>,
) : CurrentCircleLocalDataSource {
    override fun observeSelectedCircleId(): Flow<String?> = dataStore.data.map { state ->
        state.selectedCircleId.ifBlank { null }
    }

    override suspend fun getSelectedCircleId(): String? = dataStore.data.firstOrNull()?.selectedCircleId?.ifBlank { null }

    override suspend fun saveSelectedCircleId(circleId: String?) {
        dataStore.updateData {
            CurrentCircle(selectedCircleId = circleId.orEmpty())
        }
    }

    override suspend fun clearSelectedCircleId() {
        saveSelectedCircleId(circleId = null)
    }
}
