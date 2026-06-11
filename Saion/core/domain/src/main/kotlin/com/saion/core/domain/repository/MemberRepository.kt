package com.saion.core.domain.repository

import com.saion.core.model.result.AppResult

interface MemberRepository {
    suspend fun logout(): AppResult<Unit>
}
