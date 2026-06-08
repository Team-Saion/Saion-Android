package com.saion.core.datastore.datasource

interface AuthLocalDataSource {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    )

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun hasSession(): Boolean

    suspend fun clearTokens()
}
