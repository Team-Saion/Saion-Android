package com.saion.core.domain.repository

import com.saion.core.model.result.AppResult

interface AuthRepository {
    suspend fun loginWithKakao(idToken: String): AppResult<Unit>

    suspend fun isSignedIn(): Boolean
}
