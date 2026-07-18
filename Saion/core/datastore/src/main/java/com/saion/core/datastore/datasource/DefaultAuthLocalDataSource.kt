package com.saion.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.saion.core.datastore.model.AuthTokens
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

internal class DefaultAuthLocalDataSource @Inject constructor(private val dataStore: DataStore<AuthTokens>) : AuthLocalDataSource {
    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.updateData {
            AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
        }
    }

    override suspend fun getAccessToken(): String? = dataStore.data.firstOrNull()?.accessToken?.ifBlank { null }

    override suspend fun getRefreshToken(): String? = dataStore.data.firstOrNull()?.refreshToken?.ifBlank { null }

    override suspend fun hasSession(): Boolean = dataStore.data.firstOrNull()?.accessToken?.isNotBlank() == true

    override suspend fun clearTokens() {
        dataStore.updateData { AuthTokens(accessToken = "", refreshToken = "") }
    }
}
