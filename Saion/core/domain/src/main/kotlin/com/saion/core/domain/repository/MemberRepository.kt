package com.saion.core.domain.repository

import com.saion.core.model.member.MemberInfo
import com.saion.core.model.result.AppResult

interface MemberRepository {
    suspend fun getMyInfo(): AppResult<MemberInfo>

    suspend fun completeTerms(): AppResult<Unit>

    suspend fun completeNickname(nickname: String): AppResult<Unit>

    suspend fun logout(): AppResult<Unit>
}
