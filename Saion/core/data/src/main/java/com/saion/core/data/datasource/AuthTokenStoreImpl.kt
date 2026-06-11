package com.saion.core.data.datasource

import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.network.auth.AuthTokenStore
import javax.inject.Inject

internal class AuthTokenStoreImpl @Inject constructor(private val authLocalDataSource: AuthLocalDataSource) : AuthTokenStore {
    override suspend fun getAccessToken(): String? = authLocalDataSource.getAccessToken()

    override suspend fun getRefreshToken(): String? = authLocalDataSource.getRefreshToken()

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        authLocalDataSource.saveTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    override suspend fun clearTokens() {
        authLocalDataSource.clearTokens()
    }
}
