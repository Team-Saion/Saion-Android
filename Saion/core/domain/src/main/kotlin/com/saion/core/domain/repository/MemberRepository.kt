package com.saion.core.domain.repository

import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult

interface MemberRepository {
    suspend fun getMyInfo(): AppResult<MemberInfo>

    suspend fun updateProfile(nickname: String): AppResult<Unit>

    suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit>

    suspend fun logout(): AppResult<Unit>

    suspend fun withdraw(): AppResult<Unit>
}
