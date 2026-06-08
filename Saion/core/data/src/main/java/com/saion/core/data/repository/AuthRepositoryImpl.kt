package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.domain.repository.AuthRepository
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.AuthRemoteDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val localDataSource: AuthLocalDataSource,
    private val remoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
    override suspend fun loginWithKakao(idToken: String): AppResult<Unit> = safeRequest(
        request = { remoteDataSource.loginWithKakao(idToken = idToken) },
    ) { tokenData ->
        localDataSource.saveTokens(
            accessToken = tokenData.accessToken,
            refreshToken = tokenData.refreshToken,
        )
        AppResult.Success(Unit)
    }

    override suspend fun isSignedIn(): Boolean = localDataSource.hasSession()
}
