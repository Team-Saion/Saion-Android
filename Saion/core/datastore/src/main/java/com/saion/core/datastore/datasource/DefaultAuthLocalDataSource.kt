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

//    override suspend fun getAccessToken(): String? = dataStore.data.firstOrNull()?.accessToken?.ifBlank { null }

    override suspend fun getAccessToken(): String? =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhMWIyYzNkNC1lNWY2LTc4OTAtYWJjZC1lZjEyMzQ1Njc4OTAiLCJ0eXBlIjoiYWNjZXNzIiwicm9sZXMiOlsiTUVNQkVSIl0sImlhdCI6MTc4MjQ2MjAwMSwiZXhwIjoxNzkwMjM4MDAxfQ.Ex3WdmGWTS-7k9YY0tKZWXVawtjacz9eQRVtVKPR5aU"

    override suspend fun getRefreshToken(): String? = dataStore.data.firstOrNull()?.refreshToken?.ifBlank { null }

    override suspend fun hasSession(): Boolean = dataStore.data.firstOrNull()?.accessToken?.isNotBlank() == true

    override suspend fun clearTokens() {
        dataStore.updateData { AuthTokens(accessToken = "", refreshToken = "") }
    }
}
